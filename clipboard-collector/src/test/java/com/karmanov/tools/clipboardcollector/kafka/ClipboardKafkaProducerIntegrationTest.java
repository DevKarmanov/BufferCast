package com.karmanov.tools.clipboardcollector.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.karmanov.tools.clipboardcollector.ClipboardApp;
import com.karmanov.tools.clipboardcollector.dto.ClipboardTextSavedEvent;
import com.karmanov.tools.clipboardcollector.enums.TextType;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(classes = {ClipboardApp.class})
@Testcontainers
public class ClipboardKafkaProducerIntegrationTest {

    @Container
    static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.2.15"))
            .withEmbeddedZookeeper()
            .withEnv("KAFKA_AUTO_CREATE_TOPICS_ENABLE", "true");


    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
        registry.add("spring.kafka.producer.key-serializer", () -> "org.apache.kafka.common.serialization.StringSerializer");
        registry.add("spring.kafka.producer.value-serializer", () -> "org.apache.kafka.common.serialization.ByteArraySerializer");
    }

    @Autowired
    private ClipboardKafkaProducer producer;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private KafkaTemplate<String, byte[]> kafkaTemplate;

    private ClipboardTextSavedEvent makeEvent() {
        return new ClipboardTextSavedEvent(
                "This is a test",
                TextType.DEFAULT,
                UUID.randomUUID(),
                OffsetDateTime.now()
        );
    }


    @Test
    void shouldSendClipboardEvent() throws Exception {
        String topic = "clipboard-events";
        ClipboardTextSavedEvent event = makeEvent();
        producer.sendEvent(event);

        kafkaTemplate.flush();

        Thread.sleep(1000);

        Map<String, Object> consumerProps =
                KafkaTestUtils.consumerProps(kafkaContainer.getBootstrapServers(), "testGroup", "false");
        consumerProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        try (Consumer<String, byte[]> consumer = new KafkaConsumer<>(consumerProps)) {
            consumer.subscribe(Collections.singletonList(topic));

            consumer.poll(Duration.ofMillis(100));

            ConsumerRecord<String, byte[]> singleRecord =
                    KafkaTestUtils.getSingleRecord(consumer, topic, Duration.ofSeconds(7));

            ClipboardTextSavedEvent received =
                    mapper.readValue(singleRecord.value(), ClipboardTextSavedEvent.class);

            assertThat(received.text()).isEqualTo(event.text());
            assertThat(received.id()).isEqualTo(event.id());
        }
    }
}
