package com.kwezal.bearinmind.translation.repository;

import com.kwezal.bearinmind.translation.dto.TranslationIdentifierAndTextDto;
import com.kwezal.bearinmind.translation.model.Translation;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TranslationRepository extends JpaRepository<Translation, Long> {
    Optional<Translation> findByIdentifierAndLocale(Integer identifier, String locale);

    List<Translation> findAllByIdentifier(Integer identifier);

    List<Translation> findAllByIdentifierIn(Set<Integer> identifier);

    boolean existsByIdentifier(Integer identifier);

    void deleteAllByIdentifier(Integer identifier);

    void deleteByIdentifierAndLocale(Integer identifier, String locale);

    /**
     * Finds a translation text with a given identifier in a given locale.
     * If the translation has no text in a given locale, the text in the application locale is returned.
     *
     * @param identifier    translation identifier
     * @param locale        expected translation locale
     * @param defaultLocale application locale
     * @return translation text
     */
    default Optional<String> findTextByIdentifierAndLocaleOrDefaultLocale(
        Integer identifier,
        String locale,
        String defaultLocale
    ) {
        return defaultLocale.equals(locale)
            ? findTextByIdentifierAndDefaultLocale(identifier, defaultLocale)
            : findTextByIdentifierAndNonDefaultLocale(identifier, locale, defaultLocale);
    }

    /**
     * Finds a translation text with a given identifier in a given locale.
     * The query assumes that a given locale is the application locale.
     *
     * @param identifier translation identifier
     * @param locale     application locale
     * @return translation text
     */
    @Query(
        """
                    SELECT t.text
                    FROM Translation t
                    WHERE t.identifier = :identifier AND t.locale = :locale"""
    )
    Optional<String> findTextByIdentifierAndDefaultLocale(Integer identifier, String locale);

    /**
     * Finds a translation text with a given identifier in a given locale.
     * If the translation has no text in a given locale, the text in the application locale is returned.
     *
     * @param identifier    translation identifier
     * @param locale        expected translation locale
     * @param defaultLocale application locale
     * @return translation text
     */
    @Query(
        """
                    SELECT COALESCE(t2.text, t1.text)
                    FROM Translation t1
                    LEFT JOIN Translation t2 ON (t2.identifier = t1.identifier AND t2.locale = :locale)
                    WHERE t1.identifier = :identifier AND t1.locale = :defaultLocale"""
    )
    Optional<String> findTextByIdentifierAndNonDefaultLocale(Integer identifier, String locale, String defaultLocale);

    /**
     * Finds translation texts with a given identifiers in a given locale.
     * If any translation has no text in a given locale, the text in the application locale is returned for this translation.
     *
     * @param identifiers   translation identifiers
     * @param locale        expected translation locale
     * @param defaultLocale application locale
     * @return list of translation identifiers with text
     */
    default List<TranslationIdentifierAndTextDto> findAllIdentifierAndTextByIdentifiersAndLocaleOrDefaultLocale(
        Iterable<Integer> identifiers,
        String locale,
        String defaultLocale
    ) {
        return defaultLocale.equals(locale)
            ? findAllIdentifierAndTextByIdentifiersAndDefaultLocale(identifiers, defaultLocale)
            : findAllIdentifierAndTextByIdentifiersAndNonDefaultLocale(identifiers, locale, defaultLocale);
    }

    /**
     * Finds translation texts with a given identifiers in a given locale.
     * The query assumes that a given locale is the application locale.
     *
     * @param identifiers translation identifiers
     * @param locale      application locale
     * @return list of translation identifiers with text
     */
    @Query(
        """
                    SELECT new com.kwezal.bearinmind.translation.dto.TranslationIdentifierAndTextDto(t.identifier, t.text)
                    FROM Translation t
                    WHERE t.locale = :locale AND t.identifier IN :identifiers"""
    )
    List<TranslationIdentifierAndTextDto> findAllIdentifierAndTextByIdentifiersAndDefaultLocale(
        Iterable<Integer> identifiers,
        String locale
    );

    /**
     * Finds translation texts with a given identifiers in a given locale.
     * If any translation has no text in a given locale, the text in the application locale is returned for this translation.
     *
     * @param identifiers   translation identifiers
     * @param locale        expected translation locale
     * @param defaultLocale application locale
     * @return list of translation identifiers with text
     */
    @Query(
        """
                    SELECT new com.kwezal.bearinmind.translation.dto.TranslationIdentifierAndTextDto(t1.identifier, COALESCE(t2.text, t1.text))
                    FROM Translation t1
                    LEFT JOIN Translation t2 ON (t2.identifier = t1.identifier AND t2.locale = :locale)
                    WHERE t1.locale = :defaultLocale AND t1.identifier IN :identifiers"""
    )
    List<TranslationIdentifierAndTextDto> findAllIdentifierAndTextByIdentifiersAndNonDefaultLocale(
        Iterable<Integer> identifiers,
        String locale,
        String defaultLocale
    );
}
