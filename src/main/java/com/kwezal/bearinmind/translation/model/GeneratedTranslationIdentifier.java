package com.kwezal.bearinmind.translation.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.hibernate.annotations.ValueGenerationType;

@ValueGenerationType(generatedBy = TranslationIdentifierGenerator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
@interface GeneratedTranslationIdentifier {
}
