package com.karmanov.storage.service.common;

import com.karmanov.storage.component.exception.RollbackData;
import com.karmanov.storage.component.mapper.EntityMapper;
import com.karmanov.storage.component.ttl.TtlManagerImpl;
import com.karmanov.storage.dto.ClipboardText;
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
    private final EntityMapper entityMapper;
    private final RollbackData rollbackData;

    private static final Logger logger = LoggerFactory.getLogger(CommonServiceImpl.class);

    public CommonServiceImpl(PostgresServiceImpl postgresService,
                             H2ServiceImpl h2Service,
                             TtlManagerImpl ttlManagerImpl,
                             EntityMapper entityMapper,
                             RollbackData rollbackData) {
        this.postgresService = postgresService;
        this.h2Service = h2Service;
        this.ttlManagerImpl = ttlManagerImpl;
        this.entityMapper = entityMapper;
        this.rollbackData = rollbackData;
    }

    @Override
    public void save(ClipboardText event) {
        try {
            h2Service.save(event);
            postgresService.save(event);
        }
        catch(DataAccessException e) {
            rollbackData.rollbackSaving(event, e);
        }
        catch (Exception e) {
            logger.error("Unknown error: {}", e.getMessage(), e);
        }
    }

    @Override
    public void deleteById(UUID id) {
        if (id == null) {
            logger.error("Attempt to call delete with null ID");
            throw new IllegalArgumentException("ID must not be null");
        }

        TextEntity backup = h2Service.findById(id);
        ClipboardText dtoBackup = entityMapper.TextEntityToClipboardText(backup);

        try {
            h2Service.deleteById(id);
            postgresService.deleteById(id);
        }
        catch (DataAccessException e){
            rollbackData.rollbackDeletion(dtoBackup, e);
        }
        catch (Exception e) {
            logger.error("Unknown error: {}", e.getMessage(), e);
        }
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
                ClipboardText dtoBackup = entityMapper.TextEntityToClipboardText(textEntity);

                if (isExpired(textEntity)) {
                    try {
                        h2Service.delete(textEntity);
                        postgresService.delete(textEntity);
                    } catch (DataAccessException e) {
                        rollbackData.rollbackDeletion(dtoBackup, e);
                    }
                    catch (Exception e) {
                        logger.error("Unknown error: {}", e.getMessage(), e);
                    }
                }
            }
        }
        catch (DataAccessException ex) {
            logger.error("Access error", ex);
        }
    }
}
