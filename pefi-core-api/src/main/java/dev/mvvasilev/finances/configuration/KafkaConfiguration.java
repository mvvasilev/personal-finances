package dev.mvvasilev.finances.configuration;

import dev.mvvasilev.common.dto.KafkaReplaceProcessedTransactionsDTO;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Map;

@Configuration
public class KafkaConfiguration {

    public static final String REPLACE_TRANSACTIONS_TOPIC = "pefi.transactions.replace";

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Bean
    public ConsumerFactory<String, KafkaReplaceProcessedTransactionsDTO> replaceTransactionsConsumerFactory() {
        // ...
        return new DefaultKafkaConsumerFactory<>(
                Map.of(
                        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress,
                        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class
                ),
                new StringDeserializer(),
                new JsonDeserializer<>(KafkaReplaceProcessedTransactionsDTO.class)
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, KafkaReplaceProcessedTransactionsDTO> replaceTransactionsKafkaListenerContainerFactory(
            ConsumerFactory<String, KafkaReplaceProcessedTransactionsDTO> replaceTransactionsConsumerFactory
    ) {

        ConcurrentKafkaListenerContainerFactory<String, KafkaReplaceProcessedTransactionsDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(replaceTransactionsConsumerFactory);
        return factory;
    }

}
