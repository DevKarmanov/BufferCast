package com.karmanov.storage.service.storage;

import com.karmanov.storage.dto.StorageTextSavedEvent;
import com.karmanov.storage.model.TextEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StorageService {
    void saveToH2 (StorageTextSavedEvent dtoEntity);
    Optional<TextEntity> findById (UUID id);
    void delete (UUID id);
    boolean isExpired(TextEntity text);
    void clearExpired(List<TextEntity> textEntities);




}
