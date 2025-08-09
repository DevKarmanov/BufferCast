package com.karmanov.storage.service.db;

import com.karmanov.storage.component.mapper.EntityMapper;
import com.karmanov.storage.dto.StorageTextSavedEvent;
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
    @Transactional("h2TransactionManager")
    public void save(StorageTextSavedEvent dtoEntity) {
        TextEntity entity = entityMapper.StotageTextSavedToTextEntity(dtoEntity);
        h2TextRepository.save(entity);
        logger.info("Saved successfully: {}", entity.getId());
    }

    @Override
    @Transactional("h2TransactionManager")
    public void deleteById(UUID id) {
        try {
            h2TextRepository.deleteById(id);
            logger.info("Deleted by id: {} is successfully", id);
        } catch (DataAccessException ex) {
            logger.error("Access error in H2", ex);
            throw ex;
        }
    }

    @Override
    @Transactional("h2TransactionManager")
    public void delete(TextEntity text) {
        try {
            h2TextRepository.delete(text);
            logger.info("Deleted text: {} is successfully", text.getContent());
        } catch (DataAccessException ex) {
            logger.error("Access error in H2", ex);
        }
    }
}
