package com.karmanov.storage.service.db;

import com.karmanov.storage.component.mapper.EntityMapper;
import com.karmanov.storage.dto.StorageTextSavedEvent;
import com.karmanov.storage.model.TextEntity;
import com.karmanov.storage.repo.postgres.PostgresTextRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;


@Service
public class PostgresServiceImpl implements DbService {
    private final EntityMapper entityMapper;
    private final PostgresTextRepository postgresTextRepository;
    private static final Logger logger = LoggerFactory.getLogger(PostgresServiceImpl.class);

    public PostgresServiceImpl(EntityMapper entityMapper, PostgresTextRepository postgresTextRepository) {
        this.entityMapper = entityMapper;
        this.postgresTextRepository = postgresTextRepository;
    }

    @Override
    @Async
    @Transactional
    public void save(StorageTextSavedEvent dtoEntity) {
        TextEntity entity = entityMapper.mapToEntity(dtoEntity);
        postgresTextRepository.save(entity);
        logger.info("Saved to Postgres: {}", entity);
    }

    @Override
    @Async
    @Transactional
    public void deleteById(UUID id) {
        try {
            postgresTextRepository.deleteById(id);
            logger.info("Deleted by id: {} is successfully", id);
        } catch (DataAccessException ex) {
            logger.error("Access error in Postgres", ex);
        }
    }

    @Override
    @Async
    @Transactional
    public void delete(TextEntity text) {
        try {
            postgresTextRepository.delete(text);
            logger.info("Deleted text: {} is successfully", text.getContent());
        } catch (DataAccessException ex) {
            logger.error("Access error in Postgres", ex);
        }
    }

    @Async
    public CompletableFuture<Optional<TextEntity>> findById(UUID id) {
        try {
            Optional<TextEntity> result = postgresTextRepository.findById(id);
            return CompletableFuture.completedFuture(result);
        } catch (DataAccessException ex) {
            logger.error("Error accessing the Postgres repository", ex);
            return CompletableFuture.completedFuture(Optional.empty());
        }
    }
}
