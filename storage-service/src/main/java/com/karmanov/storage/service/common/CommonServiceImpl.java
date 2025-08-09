package com.karmanov.storage.service.common;

import com.karmanov.storage.component.ttl.TtlManagerImpl;
import com.karmanov.storage.dto.StorageTextSavedEvent;
import com.karmanov.storage.model.TextEntity;
import com.karmanov.storage.service.db.H2ServiceImpl;
import com.karmanov.storage.service.db.PostgresServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Service
public class CommonServiceImpl implements CommonService {
    private final PostgresServiceImpl postgresService;
    private final H2ServiceImpl h2Service;
    private final TtlManagerImpl ttlManagerImpl;

    private static final Logger logger = LoggerFactory.getLogger(CommonServiceImpl.class);

    public CommonServiceImpl(PostgresServiceImpl postgresService,
                             H2ServiceImpl h2Service,
                             TtlManagerImpl ttlManagerImpl) {
        this.postgresService = postgresService;
        this.h2Service = h2Service;
        this.ttlManagerImpl = ttlManagerImpl;
    }

    @Override
    public void save(StorageTextSavedEvent event) {
        try {
            h2Service.save(event);
            postgresService.save(event);
        }
        catch(Exception e) {
            logger.error("Error: {} - {}", e.getClass().getName(), e.getMessage(), e);
        }
    }

    @Override
    public void deleteById(UUID id) {
        if (id == null) {
            logger.error("Attempt to call delete with null ID");
            throw new IllegalArgumentException("ID must not be null");
        }
        h2Service.deleteById(id);
        postgresService.deleteById(id);
    }

    @Override
    public Optional<TextEntity> findById(UUID id) {
        if (id == null) {
            logger.error("Attempt to call findById with null ID");
            throw new IllegalArgumentException("ID must not be null");
        }

        try {
            return postgresService.findById(id).get();
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Failed to retrieve TextEntity by ID: {}", id, e);
            return Optional.empty();
        }
    }

    @Override
    public boolean isExpired(TextEntity text) {
        if (text == null) {
            logger.warn("Null text passed to isExpired — returning false as fallback");
            return false;
        }
        try {
            ttlManagerImpl.isExpired(text);
            return true;
        } catch (DataAccessException ex) {
            logger.error("Access error", ex);
            return false;
        }
    }

    @Override
    public void clearExpired(List<TextEntity> textEntities) {
        if (textEntities == null) {
            logger.error("Attempt to call Delete with null textEntities");
            throw new IllegalArgumentException("TextEntities must not be null");
        }
        try {
            for (TextEntity textEntity : textEntities) {
                if (isExpired(textEntity)) {
                    h2Service.delete(textEntity);
                    postgresService.delete(textEntity);
                }
            }
        } catch (DataAccessException ex) {
            logger.error("Access error", ex);
        }
    }
}
