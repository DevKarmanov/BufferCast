package com.karmanov.storage.component.ttl;

import com.karmanov.storage.enums.TextType;
import com.karmanov.storage.model.TextEntity;

import java.time.Duration;
import java.time.OffsetDateTime;

public interface TtlManager {
    Duration resolveTtlForType(TextType type);
    boolean isExpired(TextEntity textEntity);
    OffsetDateTime getExpiresAt(TextEntity textEntity);
}
