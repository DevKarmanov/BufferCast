package com.karmanov.storage.service.common;

import com.karmanov.storage.component.exception.RollbackData;
import com.karmanov.storage.component.mapper.EntityMapper;
import com.karmanov.storage.component.ttl.TtlManagerImpl;
import com.karmanov.storage.dto.ClipboardText;
import com.karmanov.storage.enums.TextType;
import com.karmanov.storage.model.TextEntity;
import com.karmanov.storage.service.db.H2ServiceImpl;
import com.karmanov.storage.service.db.PostgresServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommonServiceImplTest {
    @Mock
    private PostgresServiceImpl postgresService;

    @Mock
    private H2ServiceImpl h2Service;

    @Mock
    private TtlManagerImpl ttlManagerImpl;

    @Mock
    private EntityMapper entityMapper;

    @Mock
    private RollbackData rollbackData;

    @Spy
    @InjectMocks
    private CommonServiceImpl commonService;

    private UUID id;
    private TextEntity entity;
    private TextEntity entity2;
    private ClipboardText dtoBackup;
    private ClipboardText dtoBackup2;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        entity = new TextEntity();
        entity.setId(id);

        UUID id2 = UUID.randomUUID();
        entity2 = new TextEntity();
        entity2.setId(id2);

        dtoBackup = new ClipboardText("test", TextType.DEFAULT, id, OffsetDateTime.now());
        dtoBackup2 = new ClipboardText("test2", TextType.DEFAULT, id2, OffsetDateTime.now());
    }

    private ClipboardText addClipboardText(){
        String text = "This is a test";
        TextType type = TextType.DEFAULT;
        UUID id = UUID.randomUUID();
        OffsetDateTime date = OffsetDateTime.now();
        return new ClipboardText(text, type, id, date);
    }

    @Test
    void save_whenBothServicesSucceed_shouldCallBothAndNotRollback() {
        commonService.save(dtoBackup);

        verify(h2Service).save(dtoBackup);
        verify(postgresService).save(dtoBackup);
        verify(rollbackData, never()).rollbackSaving(any(), any());
    }

    @Test
    void save_whenH2Fails_shouldRollbackAndNotCallPostgres() {
        doThrow(new DataAccessResourceFailureException("H2 error"))
                .when(h2Service).save(dtoBackup);

        commonService.save(dtoBackup);

        verify(h2Service).save(dtoBackup);
        verify(postgresService, never()).save(any());
        verify(rollbackData).rollbackSaving(eq(dtoBackup), any(DataAccessException.class));
    }

    @Test
    void save_whenPostgresFails_shouldCallRollback() {
        doThrow(new DataAccessResourceFailureException("Postgres error"))
                .when(postgresService).save(dtoBackup);

        commonService.save(dtoBackup);

        verify(h2Service).save(dtoBackup);
        verify(postgresService).save(dtoBackup);
        verify(rollbackData).rollbackSaving(eq(dtoBackup), any(DataAccessException.class));
    }

    @Test
    void save_whenUnknownErrorOccurs_shouldLogErrorAndNotRollback() {
        ClipboardText event = addClipboardText();
        doThrow(new RuntimeException("Unknown error"))
                .when(postgresService).save(event);

        commonService.save(event);

        verify(h2Service).save(event);
        verify(postgresService).save(event);
        verify(rollbackData, never()).rollbackSaving(any(), any());
    }

    @Test
    void deleteById_whenBothDeletionsSucceed_shouldCallBothServices() {
        when(h2Service.findById(id)).thenReturn(entity);
        when(entityMapper.TextEntityToClipboardText(entity)).thenReturn(dtoBackup);

        commonService.deleteById(id);

        verify(h2Service).deleteById(id);
        verify(postgresService).deleteById(id);
        verifyNoInteractions(rollbackData);
    }

    @Test
    void deleteById_whenPostgresThrowsDataAccessException_shouldRollback() {
        when(h2Service.findById(id)).thenReturn(entity);
        when(entityMapper.TextEntityToClipboardText(entity)).thenReturn(dtoBackup);

        doThrow(new DataAccessResourceFailureException("DB error"))
                .when(postgresService).deleteById(id);

        commonService.deleteById(id);

        verify(rollbackData).rollbackDeletion(eq(dtoBackup), any(DataAccessException.class));
    }

    @Test
    void deleteById_whenH2ThrowsDataAccessException_shouldRollback() {
        when(h2Service.findById(id)).thenReturn(entity);
        when(entityMapper.TextEntityToClipboardText(entity)).thenReturn(dtoBackup);

        doThrow(new DataAccessResourceFailureException("DB error"))
                .when(h2Service).deleteById(id);

        commonService.deleteById(id);

        verify(rollbackData).rollbackDeletion(eq(dtoBackup), any(DataAccessException.class));
        verify(postgresService, never()).deleteById(id); // postgres даже не должен вызваться
    }

    @Test
    void deleteById_whenUnknownException_shouldNotRollback() {
        when(h2Service.findById(id)).thenReturn(entity);
        when(entityMapper.TextEntityToClipboardText(entity)).thenReturn(dtoBackup);

        doThrow(new RuntimeException("Unexpected"))
                .when(postgresService).deleteById(id);

        commonService.deleteById(id);

        verify(rollbackData, never()).rollbackDeletion(any(), any());
    }

    @Test
    void findById_whenEntityExists_shouldReturnEntity() {
        when(postgresService.findById(id))
                .thenReturn(CompletableFuture.completedFuture(Optional.of(entity)));

        Optional<TextEntity> result = commonService.findById(id);

        assertTrue(result.isPresent());
        assertEquals(id, result.get().getId());
    }

    @Test
    void findById_whenExecutionException_shouldReturnEmpty() {
        when(postgresService.findById(id))
                .thenReturn(CompletableFuture.failedFuture(
                        new ExecutionException(new RuntimeException("DB error"))));

        Optional<TextEntity> result = commonService.findById(id);

        assertTrue(result.isEmpty());
    }

    @Test
    void findById_whenInterruptedException_shouldReturnEmpty() {
        when(postgresService.findById(id))
                .thenReturn(CompletableFuture.failedFuture(
                        new InterruptedException("Interrupted")));

        Optional<TextEntity> result = commonService.findById(id);

        assertTrue(result.isEmpty());
    }

    @Test
    void findById_whenIdIsNull_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> commonService.findById(null));
    }

    @Test
    void isExpired_whenTextIsNull_shouldReturnFalse() {
        boolean result = commonService.isExpired(null);

        assertFalse(result);
        verify(ttlManagerImpl, never()).isExpired(any());
    }

    @Test
    void isExpired_whenTtlManagerSucceeds_shouldReturnTrue() {
        when(ttlManagerImpl.isExpired(entity)).thenReturn(true);

        boolean result = commonService.isExpired(entity);

        assertTrue(result);
        verify(ttlManagerImpl).isExpired(entity);
    }

    @Test
    void isExpired_whenTtlManagerThrowsDataAccessException_shouldReturnFalse() {
        doThrow(new DataAccessResourceFailureException("fail"))
                .when(ttlManagerImpl).isExpired(entity);

        boolean result = commonService.isExpired(entity);

        assertFalse(result);
        verify(ttlManagerImpl).isExpired(entity);
    }

    @Test
    void clearExpired_whenListIsNull_shouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> commonService.clearExpired(null));
        verifyNoInteractions(h2Service, postgresService, rollbackData, entityMapper);
    }

    @Test
    void clearExpired_whenListIsEmpty_shouldDoNothing() {
        commonService.clearExpired(Collections.emptyList());
        verifyNoInteractions(h2Service, postgresService, rollbackData, entityMapper);
    }

    @Test
    void clearExpired_whenAllNotExpired_shouldNotDelete() {
        when(entityMapper.TextEntityToClipboardText(entity))
                .thenReturn(new ClipboardText("text1", TextType.DEFAULT, entity.getId(), entity.getCreatedAt()));
        when(entityMapper.TextEntityToClipboardText(entity2))
                .thenReturn(new ClipboardText("text2", TextType.DEFAULT, entity2.getId(), entity2.getCreatedAt()));

        List<TextEntity> list = List.of(entity, entity2);
        doReturn(false).when(commonService).isExpired(any());

        commonService.clearExpired(list);

        verify(commonService, times(2)).isExpired(any());
        verifyNoInteractions(h2Service, postgresService, rollbackData);
    }

    @Test
    void clearExpired_whenSomeExpired_shouldDeleteOnlyExpired() {
        when(entityMapper.TextEntityToClipboardText(entity)).thenReturn(dtoBackup);
        when(entityMapper.TextEntityToClipboardText(entity2)).thenReturn(dtoBackup2);

        List<TextEntity> list = List.of(entity, entity2);
        doReturn(true).when(commonService).isExpired(entity);
        doReturn(false).when(commonService).isExpired(entity2);

        commonService.clearExpired(list);

        verify(h2Service).delete(entity);
        verify(postgresService).delete(entity);
        verify(h2Service, never()).delete(entity2);
        verify(postgresService, never()).delete(entity2);
    }

    @Test
    void clearExpired_whenDataAccessExceptionDuringDelete_shouldRollback() {
        when(entityMapper.TextEntityToClipboardText(entity)).thenReturn(dtoBackup);

        List<TextEntity> list = List.of(entity);
        doReturn(true).when(commonService).isExpired(entity);
        doThrow(new DataAccessResourceFailureException("H2 fail")).when(h2Service).delete(entity);

        commonService.clearExpired(list);

        verify(rollbackData).rollbackDeletion(eq(dtoBackup), any(DataAccessException.class));
    }

    @Test
    void clearExpired_whenUnexpectedExceptionDuringDelete_shouldLogButNotRollback() {
        when(entityMapper.TextEntityToClipboardText(entity)).thenReturn(dtoBackup);

        List<TextEntity> list = List.of(entity);
        doReturn(true).when(commonService).isExpired(entity);
        doThrow(new RuntimeException("Unknown error")).when(h2Service).delete(entity);

        commonService.clearExpired(list);

        verify(rollbackData, never()).rollbackDeletion(any(), any());
        verify(postgresService, never()).delete(entity);
    }
}
