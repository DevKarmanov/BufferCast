package com.karmanov.storage.component.mapper;

import com.karmanov.storage.enums.TextType;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface TextData {
    String text();
    UUID id();
    TextType type();
    OffsetDateTime date();
}