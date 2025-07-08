package com.karmanov.tools.clipboardcollector.dto;

import com.karmanov.tools.clipboardcollector.enums.TextType;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ClipboardTextSavedEvent(String text, TextType type, UUID id, OffsetDateTime date) {}
