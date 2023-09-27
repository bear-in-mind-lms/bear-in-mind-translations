package com.kwezal.bearinmind.translation;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TestConstants {

    public static final int TRANSLATION_IDENTIFIER_SEQUENCE_START = 1_000_001;
    public static final int NONEXISTENT_TRANSLATION_IDENTIFIER = 1_000_000;
    public static final String NONEXISTENT_TRANSLATION_LOCALE = "zh";
}
