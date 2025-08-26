package com.karmanov.storage.service.db;

import com.karmanov.storage.component.mapper.EntityMapper;
import com.karmanov.storage.dto.ClipboardText;
import com.karmanov.storage.model.TextEntity;
import com.karmanov.storage.repo.h2.H2TextRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class H2ServiceImpl implements DbService {
    private final EntityMapper entityMapper;
    private final H2TextRepository h2TextRepository;

    private static final Logger logger = LoggerFactory.getLogger(H2ServiceImpl.class);

    public H2ServiceImpl(EntityMapper entityMapper, H2TextRepository h2TextRepository) {
        this.entityMapper = entityMapper;
        this.h2TextRepository = h2TextRepository;
    }

    @Override
    @Transactional
    public void save(ClipboardText dtoEntity) {
        TextEntity entity = entityMapper.DtoToTextEntity(dtoEntity);
        try{
            h2TextRepository.save(entity);
            logger.info("Saved successfully: {}", entity.getId());
        }
        catch(DataAccessException e){
            logger.error("Failed to save entity to H2: {}", entity.getId(), e);
        }
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        try {
            h2TextRepository.deleteById(id);
            logger.info("Deleted by id: {} is successfully", id);
        } catch (DataAccessException ex) {
            logger.error("Access error in H2", ex);
        }
    }

    @Override
    @Transactional
    public void delete(TextEntity text) {
        try {
            h2TextRepository.delete(text);
            logger.info("Deleted text: {} is successfully", text.getContent());
        } catch (DataAccessException ex) {
            logger.error("Access error in H2", ex);
        }
    }

    public TextEntity findById(UUID id) {
        return h2TextRepository.findById(id).orElse(null);
    }
}
