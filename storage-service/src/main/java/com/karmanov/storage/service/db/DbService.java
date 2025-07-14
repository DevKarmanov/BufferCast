package com.karmanov.storage.service.db;

import com.karmanov.storage.dto.StorageTextSavedEvent;
import com.karmanov.storage.model.TextEntity;

import java.util.List;
import java.util.UUID;

public interface DbService {
    void save (StorageTextSavedEvent dtoEntity);
    void deleteById (UUID id);
    void delete(TextEntity text);
}
