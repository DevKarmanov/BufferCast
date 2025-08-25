package com.karmanov.storage.component.kafka;

import com.karmanov.storage.dto.ClipboardText;
import com.karmanov.storage.enums.TextType;
import com.karmanov.storage.service.common.CommonService;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {TextEventConsumer.class})
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
@ActiveProfiles("test")
@EnableKafka
public class TextEventConsumerIntegrationTest {

    static KafkaContainer kafka =
            new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.6.1"));

    static {
        kafka.start();
    }

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);

        registry.add("spring.kafka.consumer.key-deserializer",
                () -> "org.apache.kafka.common.serialization.StringDeserializer");
        registry.add("spring.kafka.consumer.value-deserializer",
                () -> "org.springframework.kafka.support.serializer.JsonDeserializer");
        registry.add("spring.kafka.consumer.properties.spring.json.trusted.packages",
                () -> "*");
        registry.add("spring.kafka.consumer.auto-offset-reset", () -> "earliest");
        registry.add("spring.kafka.consumer.group-id", () -> "storage-service");
    }

    @MockBean
    private CommonService commonService;

    private ClipboardText addClipboardText() {
        return new ClipboardText(
                "This is a test",
                TextType.DEFAULT,
                UUID.randomUUID(),
                OffsetDateTime.now()
        );
    }

    @Test
    void shouldConsumeClipboardEvent() throws Exception {
        try (AdminClient admin = AdminClient.create(Map.of("bootstrap.servers", kafka.getBootstrapServers()))) {
            NewTopic topic = new NewTopic("clipboard-events", 1, (short) 1);
            try {
                admin.createTopics(Set.of(topic)).all().get();
            } catch (ExecutionException ee) {
                Throwable cause = ee.getCause();
                if (cause instanceof org.apache.kafka.common.errors.TopicExistsException) {
                    System.out.println("Topic 'clipboard-events' already exists, skipping creation.");
                } else {
                    throw ee;
                }
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                throw ie;
            }
        }

        Map<String, Object> senderProps = KafkaTestUtils.producerProps(kafka.getBootstrapServers());
        Properties props = new Properties();
        props.putAll(senderProps);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.springframework.kafka.support.serializer.JsonSerializer");

        try (KafkaProducer<String, ClipboardText> producer = new KafkaProducer<>(props)) {
            ClipboardText dto = addClipboardText();
            producer.send(new ProducerRecord<>("clipboard-events", dto.id().toString(), dto)).get();
        }

        ArgumentCaptor<ClipboardText> captor = ArgumentCaptor.forClass(ClipboardText.class);

        await().atMost(7, TimeUnit.SECONDS).untilAsserted(() ->
                verify(commonService, Mockito.times(1)).save(captor.capture())
        );

        ClipboardText captured = captor.getValue();
        assertThat(captured.text()).isEqualTo("This is a test");
    }
}
