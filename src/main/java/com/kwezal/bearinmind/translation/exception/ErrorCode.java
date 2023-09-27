package com.kwezal.bearinmind.translation.exception;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ErrorCode {

    // BAD REQUEST
    public static final String NO_APPLICATION_LOCALE_TRANSLATION = "NO_APPLICATION_LOCALE_TRANSLATION";
    public static final String NO_REQUIRED_FIELD_IN_APPLICATION_LOCALE = "NO_REQUIRED_FIELD_IN_APPLICATION_LOCALE";
    public static final String INVALID_TRANSLATION_FIELD = "INVALID_TRANSLATION_FIELD";
    public static final String OPTIONAL_FIELD_DEFINED_BUT_NOT_PRESENT_IN_APPLICATION_LOCALE =
        "OPTIONAL_FIELD_DEFINED_BUT_NOT_PRESENT_IN_APPLICATION_LOCALE";
}
