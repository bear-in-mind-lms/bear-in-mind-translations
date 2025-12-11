package com.kwezal.bearinmind.translation;

import com.kwezal.bearinmind.translation.mapper.TranslationMapper;
import com.kwezal.bearinmind.translation.mapper.TranslationMapperImpl;
import com.kwezal.bearinmind.translation.repository.TranslationRepository;
import com.kwezal.bearinmind.translation.service.TranslationService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@AutoConfigurationPackage(basePackages = { "com.kwezal.bearinmind.translation" })
public class BearInMindTranslationsConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public TranslationMapper translationMapper() {
        return new TranslationMapperImpl();
    }

    @Bean
    @ConditionalOnMissingBean
    public TranslationService translationService(
        TranslationRepository translationRepository,
        TranslationMapper translationMapper
    ) {
        return new TranslationService(translationRepository, translationMapper);
    }
}
