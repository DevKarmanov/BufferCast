package com.karmanov.storage.service.common;

import com.karmanov.storage.dto.ClipboardText;
import com.karmanov.storage.model.TextEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CommonService {
    void save(ClipboardText event);
    void deleteById(UUID id);
    Optional<TextEntity> findById(UUID id);
    boolean isExpired(TextEntity text);
    void clearExpired(List<TextEntity> textEntities);
}
