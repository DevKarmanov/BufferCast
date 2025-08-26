package com.karmanov.storage.controller;

import com.karmanov.storage.enums.TextType;
import com.karmanov.storage.model.TextEntity;
import com.karmanov.storage.service.common.CommonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataAccessException;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@WebFluxTest(StorageControllerImpl.class)
class StorageControllerImplTest {
    UUID id;
    TextEntity textEntity;

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private CommonService commonService;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        textEntity = addTextEntity();
    }

    public TextEntity addTextEntity() {
        TextEntity textEntity = new TextEntity();
        textEntity.setId(id);
        textEntity.setContent("This is a test");
        textEntity.setType(TextType.DEFAULT);
        textEntity.setCreatedAt(OffsetDateTime.now());
        return textEntity;
    }

    @Test
    void testDeleteById_Success() {
        doNothing().when(commonService).deleteById(id);

        webTestClient.post()
                .uri("/storage/{id}", id)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("The object was successfully deleted: " + id);
    }

    @Test
    void testDeleteById_IllegalArgumentException() {
        doThrow(new IllegalArgumentException("Object not found"))
                .when(commonService).deleteById(id);

        webTestClient.post()
                .uri("/storage/{id}", id)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class)
                .isEqualTo("Deletion is not possible: Object not found");
    }

    @Test
    void testDeleteById_DataAccessException() {
        doThrow(new DataAccessException("DB error") {}).when(commonService).deleteById(id);

        webTestClient.post()
                .uri("/storage/{id}", id)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(String.class)
                .isEqualTo("Database access error during deletion: " + id);
    }

    @Test
    void findById_Success() {
        doReturn(Optional.of(textEntity)).when(commonService).findById(id);

        webTestClient.get()
                .uri("/storage/{id}", id)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TextEntity.class) // а не String
                .value(response -> {
                    assertThat(response.getId()).isEqualTo(textEntity.getId());
                    assertThat(response.getContent()).isEqualTo(textEntity.getContent());
                });
    }

    @Test
    void findById_NothingFound(){
        doReturn(Optional.empty()).when(commonService).findById(id);

        webTestClient.get()
                .uri("/storage/{id}", id)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class)
                .isEqualTo("Object with ID " + id + " not found");
    }

    @Test
    void findById_IllegalArgumentException() {
        doThrow(new IllegalArgumentException("Object not found"))
                .when(commonService).findById(id);

        webTestClient.get()
                .uri("/storage/{id}", id)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class)
                .isEqualTo("Invalid ID: Object not found");
    }

    @Test
    void findById_GenericException() {
        doThrow(new RuntimeException("DB error"))
                .when(commonService).findById(id);

        webTestClient.get()
                .uri("/storage/{id}", id)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(String.class)
                .isEqualTo("Error in the search: DB error");
    }
}
