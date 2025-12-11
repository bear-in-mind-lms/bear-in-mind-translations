package com.kwezal.bearinmind.translation.dto;

import com.kwezal.bearinmind.translation.validation.annotation.Locale;
import jakarta.validation.constraints.NotNull;

public record TranslationIdentifierAndLocaleDto(
    @NotNull Integer identifier,

    @Locale String locale
) {}
