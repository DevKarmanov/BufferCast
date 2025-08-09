package com.karmanov.storage.component.mapper;

import com.karmanov.storage.dto.ClipboardText;
import com.karmanov.storage.model.TextEntity;
import org.springframework.stereotype.Component;

@Component
public class EntityMapper {
    public <T extends TextData> TextEntity DtoToTextEntity(T dto) {
        TextEntity entity = new TextEntity();
        entity.setId(dto.id());
        entity.setContent(dto.text());
        entity.setType(dto.type());
        entity.setCreatedAt(dto.date());
        return entity;
    }

    public ClipboardText TextEntityToClipboardText(TextEntity entity) {
        return new ClipboardText(entity.getContent(), entity.getType(), entity.getId(), entity.getCreatedAt());
    }
}
