package dev.mvvasilev.statements.configuration;

import dev.mvvasilev.common.dto.KafkaProcessedTransactionDTO;
import dev.mvvasilev.common.dto.KafkaReplaceProcessedTransactionsDTO;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Map;

@Configuration
public class KafkaConfiguration {

    public static final String REPLACE_TRANSACTIONS_TOPIC = "pefi.transactions.replace";

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        return new KafkaAdmin(Map.of(
                AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress
        ));
    }

    @Bean
    public NewTopic replaceTransactions() {
        return new NewTopic(REPLACE_TRANSACTIONS_TOPIC, 1, (short) 1);
    }

    @Bean
    public ProducerFactory<String, KafkaReplaceProcessedTransactionsDTO> replaceTransactionsProducerFactory() {
        return new DefaultKafkaProducerFactory<>(Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class
        ));
    }

    @Bean
    public KafkaTemplate<String, KafkaReplaceProcessedTransactionsDTO> replaceTransactionsKafkaTemplate(
            ProducerFactory<String, KafkaReplaceProcessedTransactionsDTO> replaceTransactionsProducerFactory
    ) {
        return new KafkaTemplate<>(replaceTransactionsProducerFactory);
    }

}
