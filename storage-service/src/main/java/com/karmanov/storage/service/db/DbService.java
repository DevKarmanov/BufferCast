package com.karmanov.storage.service.db;

import com.karmanov.storage.dto.ClipboardText;
import com.karmanov.storage.model.TextEntity;

import java.util.UUID;

public interface DbService {
    void save (ClipboardText dtoEntity);
    void deleteById (UUID id);
    void delete(TextEntity text);
}
