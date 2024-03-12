package georgetenev.usereventconsumer.config;

import java.util.Map;

import georgetenev.usereventconsumer.dto.UserLogin;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

public class KafkaConsumerConfig {
 
    @Value("${spring.kafka.consumer.group-id}")
    private String consumerGroupId;


    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

        @Bean
    public DefaultKafkaConsumerFactory<String, String> consumerFactory() {

        Map<String, Object> props = Map.of(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, this.bootstrapAddress,
            ConsumerConfig.GROUP_ID_CONFIG, this.consumerGroupId,
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName(),
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonSerializer.class.getName()
        );

        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}
