package com.karmanov.storage.component.exception;

import com.karmanov.storage.dto.ClipboardText;
import org.springframework.dao.DataAccessException;

public interface RollbackData {
    void rollbackDeletion(ClipboardText event, DataAccessException e);
    void rollbackSaving(ClipboardText event, DataAccessException e);
    boolean isPostgresError(DataAccessException e);

}
