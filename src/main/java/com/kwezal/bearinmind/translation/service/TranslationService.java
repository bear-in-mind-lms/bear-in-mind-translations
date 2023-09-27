package com.kwezal.bearinmind.translation.service;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.springframework.util.CollectionUtils.isEmpty;

import com.kwezal.bearinmind.exception.InvalidRequestDataException;
import com.kwezal.bearinmind.exception.ResourceNotFoundException;
import com.kwezal.bearinmind.translation.dto.TranslationIdentifierAndLocaleDto;
import com.kwezal.bearinmind.translation.dto.TranslationIdentifierAndTextDto;
import com.kwezal.bearinmind.translation.dto.TranslationTextDto;
import com.kwezal.bearinmind.translation.mapper.TranslationMapper;
import com.kwezal.bearinmind.translation.model.Translation;
import com.kwezal.bearinmind.translation.model.Translation_;
import com.kwezal.bearinmind.translation.repository.TranslationRepository;
import com.kwezal.bearinmind.translation.utils.CollectionUtils;
import com.kwezal.bearinmind.translation.validation.annotation.Locale;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class TranslationService {

    @Value("${application.locale}")
    private String applicationLocale;

    private final TranslationRepository translationRepository;
    private final TranslationMapper translationMapper;
    private final TranslationValidationService translationValidationService;

    /**
     * Creates a translation in the application locale.
     *
     * @param dto data for translation creation
     * @return created translation's identifier
     */
    @Transactional(readOnly = false)
    public Integer createTranslation(final TranslationTextDto dto) {
        var translation = translationMapper.map(dto, applicationLocale);
        translation = translationRepository.save(translation);
        return translation.getIdentifier();
    }

    /**
     * Creates a single translation in multiple locales.
     * Text can only be defined if it is also present in the application locale.
     *
     * @param localeTextMap mapping of locale to text
     * @return created translation's identifier or {@code null} if a given mapping is empty
     */
    @Transactional(readOnly = false)
    public Integer createMultilingualTranslation(final Map<@Locale String, String> localeTextMap) {
        return createMultilingualTranslation(localeTextMap, false);
    }

    /**
     * Creates a single translation in multiple locales.
     * Text can only be defined if it is also present in the application locale.
     *
     * @param localeTextMap mapping of locale to text
     * @param isRequired    flag that specifies whether a given mapping can be empty
     * @return created translation's identifier or {@code null} if a given mapping is empty
     */
    @Transactional(readOnly = false)
    public Integer createMultilingualTranslation(final Map<@Locale String, String> localeTextMap, final boolean isRequired) {
        if (!isRequired && isEmpty(localeTextMap)) {
            return null;
        }

        translationValidationService.validateIfTranslationsInLocaleExist(localeTextMap, applicationLocale);

        // Make a copy to avoid modifying the passed argument
        final var localeTextWithoutApplicationLocaleMap = new HashMap<>(localeTextMap);

        final var applicationLocaleText = localeTextWithoutApplicationLocaleMap.remove(applicationLocale);

        final var translation = translationRepository.save(translationMapper.map(applicationLocaleText, applicationLocale));
        final var identifier = translation.getIdentifier();

        if (!localeTextWithoutApplicationLocaleMap.isEmpty()) {
            final var translations = translationMapper.map(localeTextWithoutApplicationLocaleMap, identifier);
            translationRepository.saveAll(translations);
        }

        return identifier;
    }

    /**
     * Creates multiple translations in multiple locales.
     * Required fields have to be present in the application locale.
     * Optional fields can only be defined if they are also present in the application locale.
     * Fields other than required and optional are not allowed.
     *
     * @param localeFieldTextsMap mapping of locale to field texts
     * @param requiredFields      required field names
     * @param optionalFields      optional field names
     * @return mapping of field name to created translation's identifier or an empty map if a given mapping and required fields are empty
     */
    @Transactional(readOnly = false)
    public Map<String, Integer> createMultilingualTranslations(
        final Map<@Locale String, Map<String, String>> localeFieldTextsMap,
        final Set<String> requiredFields,
        final Set<String> optionalFields
    ) {
        if (localeFieldTextsMap.isEmpty() && requiredFields.isEmpty()) {
            return Map.of();
        }

        translationValidationService.validateIfTranslationsInLocaleExist(localeFieldTextsMap, applicationLocale);

        // Make a copy to avoid modifying the passed argument
        final var localeTextWithoutApplicationLocaleMap = new HashMap<>(localeFieldTextsMap);

        final var applicationLocaleFieldTextMap = localeTextWithoutApplicationLocaleMap.remove(applicationLocale);

        translationValidationService.validateIfTranslationsHaveRequiredFields(applicationLocaleFieldTextMap, requiredFields);
        translationValidationService.validateIfTranslationsContainOnlyExpectedFields(
            applicationLocaleFieldTextMap,
            requiredFields,
            optionalFields
        );
        translationValidationService.validateIfFieldIsNotDefinedIfNotPresentInLocale(
            localeTextWithoutApplicationLocaleMap,
            applicationLocaleFieldTextMap
        );

        final var fieldIdentifiers = createApplicationLocaleTranslations(applicationLocaleFieldTextMap);
        if (!localeTextWithoutApplicationLocaleMap.isEmpty()) {
            final var translations = translationMapper.map(localeTextWithoutApplicationLocaleMap, fieldIdentifiers);
            translationRepository.saveAll(translations);
        }

        return fieldIdentifiers;
    }

    /**
     * Creates multiple translations in the application locale.
     *
     * @param applicationLocaleFieldTextMap mapping of field name to text
     * @return mapping of field name to created translation's identifier
     */
    private Map<String, Integer> createApplicationLocaleTranslations(final Map<String, String> applicationLocaleFieldTextMap) {
        return applicationLocaleFieldTextMap
            .entrySet()
            .stream()
            .map(field -> {
                final var translation = translationRepository.save(translationMapper.map(field.getValue(), applicationLocale));
                return Map.entry(field.getKey(), translation.getIdentifier());
            })
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Appends a given text in a given locale to the existing translation.
     *
     * @param identifier translation identifier
     * @param locale     locale
     * @param text       translation text
     */
    @Transactional(readOnly = false)
    public void appendTranslation(final Integer identifier, final String locale, final String text) {
        requireExistsByIdentifier(identifier);
        var translation = translationMapper.map(text, locale, identifier);
        translationRepository.save(translation);
    }

    /**
     * Updates a translation of a given identifier in a given locale
     *
     * @param identifier translation identifier
     * @param locale     locale
     * @param text       translation text
     */
    @Transactional(readOnly = false)
    public void updateTranslation(final Integer identifier, final String locale, final String text) {
        final var translation = fetchTranslationByIdentifierAndLocale(identifier, locale);
        translation.setText(text);
        translationRepository.save(translation);
    }

    /**
     * Updates a single translation in multiple locales.
     *
     * @param identifier    translation identifier
     * @param localeTextMap mapping of locale to text
     */
    @Transactional(readOnly = false)
    public void updateMultilingualTranslation(final Integer identifier, final Map<@Locale String, String> localeTextMap) {
        translationValidationService.validateIfTranslationsInLocaleExist(localeTextMap, applicationLocale);

        final var translations = translationRepository.findAllByIdentifier(identifier);
        if (translations.isEmpty()) {
            throw new ResourceNotFoundException(Translation.class, Map.of(Translation_.IDENTIFIER, identifier));
        }

        // Make a copy to avoid modifying the passed argument
        final var localeTextToCreateMap = new HashMap<>(localeTextMap);

        final var translationsToSave = new ArrayList<Translation>();
        final var translationsToDelete = new ArrayList<Translation>();

        for (final var translation : translations) {
            if (localeTextToCreateMap.containsKey(translation.getLocale())) {
                final var text = localeTextToCreateMap.remove(translation.getLocale());
                if (!translation.getText().equals(text)) {
                    translation.setText(text);
                    translationsToSave.add(translation);
                }
            } else {
                translationsToDelete.add(translation);
            }
        }

        if (!translationsToDelete.isEmpty()) {
            translationRepository.deleteAll(translationsToDelete);
        }

        if (!localeTextToCreateMap.isEmpty()) {
            final var translationsToCreate = translationMapper.map(localeTextToCreateMap, identifier);
            translationsToSave.addAll(translationsToCreate);
        }

        if (!translationsToSave.isEmpty()) {
            translationRepository.saveAll(translationsToSave);
        }
    }

    /**
     * Updates multiple translations in multiple locales.
     *
     * @param fieldIdentifierMap  mapping of field name to translation identifier
     * @param localeFieldTextsMap mapping of locale to field texts
     * @return mapping of field name to updated translation's identifier
     */
    @Transactional(readOnly = false)
    public Map<String, Integer> updateMultilingualTranslations(
        final Map<String, Integer> fieldIdentifierMap,
        final Map<@Locale String, Map<String, String>> localeFieldTextsMap
    ) {
        // Make a copy to avoid modifying the passed argument
        final var result = new HashMap<>(fieldIdentifierMap);

        final var fieldLocaleTextsMap = CollectionUtils.swapMapKeys(localeFieldTextsMap);

        fieldIdentifierMap.forEach((field, identifier) -> {
            if (isNull(identifier)) {
                final var createdIdentifier = createMultilingualTranslation(fieldLocaleTextsMap.get(field));
                if (nonNull(createdIdentifier)) {
                    result.put(field, createdIdentifier);
                }
            } else {
                updateMultilingualTranslation(identifier, fieldLocaleTextsMap.get(field));
            }
        });

        return result;
    }

    /**
     * Finds a text of a translation with a given identifier in a given locale.
     *
     * @param identifier translation identifier
     * @param locale     locale
     * @return translation text
     */
    public String findTextByIdentifierAndLocale(final Integer identifier, final String locale) {
        final var text = translationRepository.findTextByIdentifierAndLocaleOrDefaultLocale(
            identifier,
            locale,
            applicationLocale
        );

        return text.orElseThrow(() ->
            new ResourceNotFoundException(
                Translation.class,
                Map.of(
                    Translation_.IDENTIFIER,
                    identifier,
                    Translation_.LOCALE,
                    applicationLocale.equals(locale) ? List.of(locale) : List.of(locale, applicationLocale)
                )
            )
        );
    }

    /**
     * Finds all translations in a given locale with identifiers obtained from a given stream mapped with a given function.
     *
     * @param stream stream
     * @param mapper function mapping stream to translation identifiers
     * @param locale locale
     * @return mapping of translation identifier to text
     */
    public <T> Map<Integer, String> findAllIdentifierAndTextByIdentifiersAndLocale(
        final Stream<T> stream,
        final Function<T, Integer> mapper,
        final String locale
    ) {
        final var identifiers = stream.map(mapper).collect(Collectors.toSet());
        return findAllIdentifierAndTextByIdentifiersAndLocale(identifiers, locale);
    }

    /**
     * Finds all translations with a given identifiers in a given locale.
     *
     * @param identifiers translation identifiers
     * @param locale      locale
     * @return mapping of translation identifier to text
     */
    public Map<Integer, String> findAllIdentifierAndTextByIdentifiersAndLocale(
        final Collection<Integer> identifiers,
        final String locale
    ) {
        if (isEmpty(identifiers)) {
            return Map.of();
        }

        final var texts = translationRepository.findAllIdentifierAndTextByIdentifiersAndLocaleOrDefaultLocale(
            identifiers,
            locale,
            applicationLocale
        );

        return texts
            .stream()
            .collect(Collectors.toMap(TranslationIdentifierAndTextDto::identifier, TranslationIdentifierAndTextDto::text));
    }

    /**
     * Deletes a translation and all its locales with a given identifier.
     *
     * @param identifier translation identifier
     */
    @Transactional(readOnly = false)
    public void deleteAllTranslationBy(final Integer identifier) {
        translationRepository.deleteAllByIdentifier(identifier);
    }

    /**
     * Deletes a translation with a given identifier in a given locale.
     * The locale cannot be the application locale.
     *
     * @param identifier translation identifier
     * @param locale     locale
     */
    @Transactional(readOnly = false)
    public void deleteTranslationByIdentifierAndLocale(final Integer identifier, final String locale) {
        // Prevent deletion of the translation in the default application locale
        if (applicationLocale.equals(locale)) {
            throw new InvalidRequestDataException(TranslationIdentifierAndLocaleDto.class, Map.of("locale", locale));
        }

        translationRepository.deleteByIdentifierAndLocale(identifier, locale);
    }

    private Translation fetchTranslationByIdentifierAndLocale(final Integer identifier, final String locale) {
        return translationRepository
            .findByIdentifierAndLocale(identifier, locale)
            .orElseThrow(() ->
                new ResourceNotFoundException(
                    Translation.class,
                    Map.of(Translation_.IDENTIFIER, identifier, Translation_.LOCALE, locale)
                )
            );
    }

    private void requireExistsByIdentifier(final Integer identifier) {
        if (!translationRepository.existsByIdentifier(identifier)) {
            throw new ResourceNotFoundException(Translation.class, Map.of(Translation_.IDENTIFIER, identifier.toString()));
        }
    }
}
