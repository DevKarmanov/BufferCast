package com.karmanov.storage.service.db;

import com.karmanov.storage.component.mapper.EntityMapper;
import com.karmanov.storage.component.mapper.TextData;
import com.karmanov.storage.dto.ClipboardText;
import com.karmanov.storage.enums.TextType;
import com.karmanov.storage.model.TextEntity;
import com.karmanov.storage.repo.h2.H2TextRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class H2ServiceImplTest {
    @Mock
    private EntityMapper entityMapper;

    @Mock
    private H2TextRepository h2TextRepository;

    @InjectMocks
    private H2ServiceImpl h2Service;

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
                .when(h2TextRepository).save(mappedEntity);

        h2Service.save(dtoEntity);

        verify(h2TextRepository).save(mappedEntity);
    }

    @Test
    void save_successfulSave_logsSuccess(){
        ClipboardText dtoEntity = addClipboardText();
        TextEntity mappedEntity = new TextEntity();
        mappedEntity.setId(dtoEntity.id());

        when(entityMapper.DtoToTextEntity(dtoEntity)).thenReturn(mappedEntity);
        when(h2TextRepository.save(mappedEntity)).thenReturn(mappedEntity);

        h2Service.save(dtoEntity);

        verify(entityMapper).DtoToTextEntity(dtoEntity);
        verify(h2TextRepository).save(mappedEntity);

        assertEquals(dtoEntity.id(), mappedEntity.getId());
    }

    @Test
    void deleteById_successfulDelete_logsSuccess(){
        UUID id = UUID.randomUUID();

        h2Service.deleteById(id);

        verify(h2TextRepository).deleteById(id);
    }

    @Test
    void deleteById_whenRepositoryThrowsDataAccessException_shouldLogError(){
        UUID id = UUID.randomUUID();

        doThrow(new DataAccessResourceFailureException("DB error"))
                .when(h2TextRepository).deleteById(id);

        h2Service.deleteById(id);

        verify(h2TextRepository).deleteById(id);
    }

    @Test
    void delete_successfulDelete_logsSuccess(){
        TextEntity entity = new TextEntity();

        h2Service.delete(entity);

        verify(h2TextRepository).delete(entity);
    }

    @Test
    void delete_whenRepositoryThrowsDataAccessException_shouldLogError(){
        TextEntity entity = new TextEntity();

        doThrow(new DataAccessResourceFailureException("DB error"))
                .when(h2TextRepository).delete(entity);

        h2Service.delete(entity);

        verify(h2TextRepository).delete(entity);
    }

    @Test
    void findById_whenEntityExists_shouldReturnEntity(){
        UUID id = UUID.randomUUID();
        TextEntity entity = new TextEntity();
        entity.setId(id);

        when(h2TextRepository.findById(id)).thenReturn(Optional.of(entity));

        TextEntity result = h2Service.findById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(h2TextRepository).findById(id);
    }

    @Test
    void findById_whenEntityDoesNotExist_shouldReturnNull(){
        UUID id = UUID.randomUUID();

        when(h2TextRepository.findById(id)).thenReturn(Optional.empty());

        TextEntity result = h2Service.findById(id);

        assertNull(result);
        verify(h2TextRepository).findById(id);
    }

    @Test
    void findById_whenRepositoryThrowsException_shouldThrowException() {
        UUID id = UUID.randomUUID();

        when(h2TextRepository.findById(id))
                .thenThrow(new DataAccessResourceFailureException("DB error"));

        assertThrows(DataAccessException.class, () -> h2Service.findById(id));
    }
}
