package com.karmanov.storage.model;

import com.karmanov.storage.enums.TextType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "clipboard_texts")
@Getter
@Setter
@NoArgsConstructor
public class TextEntity {
    @Id
    private UUID id;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TextType type;

    @CreationTimestamp
    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE", nullable = false)
    private OffsetDateTime createdAt;

    public TextEntity(String content, TextType type, OffsetDateTime createdAt) {
        this.content = content;
        this.type = type;
        this.createdAt = createdAt;
    }
}
