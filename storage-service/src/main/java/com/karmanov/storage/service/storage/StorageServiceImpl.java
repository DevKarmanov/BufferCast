package com.karmanov.storage.service.storage;

import com.karmanov.storage.dto.StorageTextSavedEvent;
import com.karmanov.storage.model.TextEntity;
import com.karmanov.storage.repo.h2.H2TextRepository;
import com.karmanov.storage.service.async.PostgresAsyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class StorageServiceImpl implements StorageService{
    private final H2TextRepository h2TextRepository;
    private final PostgresAsyncService postgresAsyncService;
    private static final Logger logger = LoggerFactory.getLogger(StorageServiceImpl.class);

    public StorageServiceImpl(H2TextRepository h2TextRepository, PostgresAsyncService postgresAsyncService) {
        this.h2TextRepository = h2TextRepository;
        this.postgresAsyncService = postgresAsyncService;
    }

    @Override
    public void saveToH2(StorageTextSavedEvent dtoEntity) {
        try {
            TextEntity entity = mapToEntity(dtoEntity);
            logger.info("H2 connected? Count: {}", h2TextRepository.count());
            h2TextRepository.save(entity);
            logger.info("Saved to H2: {}", dtoEntity.id());
            postgresAsyncService.saveAsync(entity);

        }
        catch(Exception e) {
            logger.error("Error: {} - {}", e.getClass().getName(), e.getMessage(), e);
        }
    }

    @Override
    public Optional<TextEntity> findById(UUID id) {
        return Optional.empty();
    }

    @Override
    public void delete(UUID id) {

    }

    @Override
    public boolean isExpired(TextEntity text) {
        return false;
    }

    @Override
    public void clearExpired(List<TextEntity> textEntities) {

    }

    private TextEntity mapToEntity(StorageTextSavedEvent dto) {
        TextEntity entity = new TextEntity();
        entity.setId(dto.id());
        entity.setContent(dto.text());
        entity.setType(dto.type());
        entity.setCreatedAt(dto.date());
        return entity;
    }
}
