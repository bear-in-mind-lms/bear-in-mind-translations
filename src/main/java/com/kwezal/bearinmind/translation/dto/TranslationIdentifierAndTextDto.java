package com.kwezal.bearinmind.translation.dto;

import jakarta.validation.constraints.NotNull;

public record TranslationIdentifierAndTextDto(
    @NotNull Integer identifier,

    @NotNull String text
) {}
