package com.kwezal.bearinmind.translation.dto;

import javax.validation.constraints.NotNull;

public record TranslationIdentifierAndTextDto(
    @NotNull Integer identifier,

    @NotNull String text
) {}
