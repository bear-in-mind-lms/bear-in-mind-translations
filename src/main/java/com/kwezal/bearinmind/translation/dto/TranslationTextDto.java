package com.kwezal.bearinmind.translation.dto;

import jakarta.validation.constraints.NotNull;

public record TranslationTextDto(@NotNull String text) {}
