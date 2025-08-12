package com.karmanov.storage.component.exception;

import com.karmanov.storage.dto.ClipboardText;
import com.karmanov.storage.service.db.H2ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

@Component
public class RollbackDataImpl implements RollbackData {
    private final H2ServiceImpl h2Service;

    private static final Logger logger = LoggerFactory.getLogger(RollbackDataImpl.class);

    public RollbackDataImpl(H2ServiceImpl h2Service) {
        this.h2Service = h2Service;
    }

    @Override
    public void rollbackDeletion(ClipboardText event, DataAccessException e) {
        if (isPostgresError(e)){
            logger.error("Error deleting data in Postgres: {}. Rollback H2.", e.getMessage());
            try {
                h2Service.save(event);
            }
            catch (Exception rollbackEx) {
                logger.error("Error when rolling back H2: {}", rollbackEx.getMessage(), rollbackEx);
            }
        }
        else {
            logger.error("Error deleting data: {}", e.getMessage(), e);
        }
    }

    @Override
    public void rollbackSaving(ClipboardText event, DataAccessException e) {
        if (isPostgresError(e)){
            logger.error("Error saving in Postgres: {}. Rollback H2.", e.getMessage());
            try {
                h2Service.deleteById(event.id());
            }
            catch (Exception rollbackEx){
                logger.error("Error when rolling back H2: {}", rollbackEx.getMessage(), rollbackEx);
            }
        }
        else {
            logger.error("Error saving data: {}", e.getMessage(), e);
        }
    }

    @Override
    public boolean isPostgresError(DataAccessException e) {
        return e.getCause() instanceof org.postgresql.util.PSQLException;
    }
}
