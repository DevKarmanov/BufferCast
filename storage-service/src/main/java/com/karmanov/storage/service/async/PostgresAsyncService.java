package com.karmanov.storage.service.async;

import com.karmanov.storage.model.TextEntity;

public interface PostgresAsyncService {
    void saveAsync (TextEntity entity);
}
