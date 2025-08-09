package com.karmanov.storage.component.ttl;

import com.karmanov.storage.enums.TextType;
import com.karmanov.storage.model.TextEntity;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.OffsetDateTime;

@Component
public class TtlManagerImpl implements TtlManager {
    @Override
    public Duration resolveTtlForType(TextType type) {
        return switch (type){
            case EMAIL -> Duration.ofDays(3);
            case URL -> Duration.ofDays(4);
            case PATH -> Duration.ofHours(5);
            default -> Duration.ofDays(6);
        };
    }

    @Override
    public boolean isExpired(TextEntity textEntity) {
        Duration ttl = resolveTtlForType(textEntity.getType());
        return OffsetDateTime.now().isAfter(textEntity.getCreatedAt().plus(ttl));
    }

    @Override
    public OffsetDateTime getExpiresAt(TextEntity textEntity) {
        return textEntity.getCreatedAt().plus(resolveTtlForType(textEntity.getType()));
    }
}
