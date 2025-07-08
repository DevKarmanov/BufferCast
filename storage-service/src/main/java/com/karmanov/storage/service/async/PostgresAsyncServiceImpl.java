package com.karmanov.storage.service.async;

import com.karmanov.storage.model.TextEntity;
import com.karmanov.storage.repo.postgres.PostgresTextRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class PostgresAsyncServiceImpl implements PostgresAsyncService{
    private final PostgresTextRepository postgresTextRepository;
    private static final Logger logger = LoggerFactory.getLogger(PostgresAsyncServiceImpl.class);

    public PostgresAsyncServiceImpl(PostgresTextRepository postgresTextRepository) {
        this.postgresTextRepository = postgresTextRepository;
    }

    @Override
    @Async
    public void saveAsync(TextEntity entity) {
        postgresTextRepository.save(entity);
        logger.info("Saved to Postgres: {}", entity);
    }
}
