package com.karmanov.storage.dto;

import com.karmanov.storage.component.mapper.TextData;
import com.karmanov.storage.enums.TextType;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ClipboardText(String text, TextType type, UUID id, OffsetDateTime date) implements TextData {}
