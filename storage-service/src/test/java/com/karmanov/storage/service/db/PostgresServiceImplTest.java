package com.karmanov.storage.service.db;

import com.karmanov.storage.component.mapper.EntityMapper;
import com.karmanov.storage.component.mapper.TextData;
import com.karmanov.storage.dto.ClipboardText;
import com.karmanov.storage.enums.TextType;
import com.karmanov.storage.model.TextEntity;
import com.karmanov.storage.repo.postgres.PostgresTextRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PostgresServiceImplTest {
    @Mock
    private EntityMapper entityMapper;
    
    @Mock
    private PostgresTextRepository postgresTextRepository;
    
    @InjectMocks
    private PostgresServiceImpl postgresService;

    private ClipboardText addClipboardText(){
        String text = "This is a test";
        TextType type = TextType.DEFAULT;
        UUID id = UUID.randomUUID();
        OffsetDateTime date = OffsetDateTime.now();
        return new ClipboardText(text, type, id, date);
    }

    @Test
    void save_whenRepositoryThrowsDataAccessException_shouldLogError() {
        ClipboardText dtoEntity = addClipboardText();
        TextEntity mappedEntity = new TextEntity();

        when(entityMapper.DtoToTextEntity(any(TextData.class))).thenReturn(mappedEntity);

        doThrow(new DataAccessResourceFailureException("DB error"))
                .when(postgresTextRepository).save(mappedEntity);

        postgresService.save(dtoEntity);

        verify(postgresTextRepository).save(mappedEntity);
    }

    @Test
    void save_successfulSave_logsSuccess(){
        ClipboardText dtoEntity = addClipboardText();
        TextEntity mappedEntity = new TextEntity();
        mappedEntity.setId(dtoEntity.id());

        when(entityMapper.DtoToTextEntity(dtoEntity)).thenReturn(mappedEntity);
        when(postgresTextRepository.save(mappedEntity)).thenReturn(mappedEntity);

        postgresService.save(dtoEntity);

        verify(entityMapper).DtoToTextEntity(dtoEntity);
        verify(postgresTextRepository).save(mappedEntity);

        assertEquals(dtoEntity.id(), mappedEntity.getId());
    }

    @Test
    void deleteById_successfulDelete_logsSuccess(){
        UUID id = UUID.randomUUID();

        postgresService.deleteById(id);

        verify(postgresTextRepository).deleteById(id);
    }

    @Test
    void deleteById_whenRepositoryThrowsDataAccessException_shouldLogError(){
        UUID id = UUID.randomUUID();

        doThrow(new DataAccessResourceFailureException("DB error"))
                .when(postgresTextRepository).deleteById(id);

        postgresService.deleteById(id);

        verify(postgresTextRepository).deleteById(id);
    }

    @Test
    void delete_successfulDelete_logsSuccess(){
        TextEntity entity = new TextEntity();

        postgresService.delete(entity);

        verify(postgresTextRepository).delete(entity);
    }

    @Test
    void delete_whenRepositoryThrowsDataAccessException_shouldLogError(){
        TextEntity entity = new TextEntity();

        doThrow(new DataAccessResourceFailureException("DB error"))
                .when(postgresTextRepository).delete(entity);

        postgresService.delete(entity);

        verify(postgresTextRepository).delete(entity);
    }

    @Test
    void findById_whenEntityExists_shouldReturnEntity(){
        UUID id = UUID.randomUUID();
        TextEntity entity = new TextEntity();
        entity.setId(id);

        when(postgresTextRepository.findById(id)).thenReturn(Optional.of(entity));

        CompletableFuture<Optional<TextEntity>> resultFuture = postgresService.findById(id);
        Optional<TextEntity> resultOptional = resultFuture.join();

        assertTrue(resultOptional.isPresent());
        assertEquals(id, resultOptional.get().getId());

        verify(postgresTextRepository).findById(id);
    }

    @Test
    void findById_whenEntityDoesNotExist_shouldReturnNull(){
        UUID id = UUID.randomUUID();

        when(postgresTextRepository.findById(id)).thenReturn(Optional.empty());

        CompletableFuture<Optional<TextEntity>> resultFuture = postgresService.findById(id);
        Optional<TextEntity> resultOptional = resultFuture.join();

        assertTrue(resultOptional.isEmpty());
        verify(postgresTextRepository).findById(id);
    }

    @Test
    void findById_whenRepositoryThrowsException_shouldReturnEmpty() {
        UUID id = UUID.randomUUID();

        when(postgresTextRepository.findById(id))
                .thenThrow(new DataAccessResourceFailureException("DB error"));

        CompletableFuture<Optional<TextEntity>> resultFuture = postgresService.findById(id);
        Optional<TextEntity> result = resultFuture.join();

        assertTrue(result.isEmpty());
    }
}
