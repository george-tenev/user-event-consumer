package georgetenev.usereventconsumer.config;

import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes.StringSerde;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import georgetenev.usereventconsumer.dto.UserLogin;
import georgetenev.usereventconsumer.service.KstreamProcessor ;

@EnableKafka
@EnableKafkaStreams
@Configuration
public class KafkaStreamsConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Value("${spring.kafka.topic.logins.name}")
    private String inputTopic;

    @Value("${spring.kafka.consumer.group-id}")
    private String consumerGroupId;

    @Value("${spring.kafka.consumer.auto-offset-reset}")
    private String autoOffsetReset;

    @Value("${spring.kafka.streams.application-id}")
    private String applicationId;

    private final KstreamProcessor kstreamProcessor;

    public KafkaStreamsConfig(KstreamProcessor kstreamProcessor) {
        this.kstreamProcessor = kstreamProcessor;
    }

    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    public KafkaStreamsConfiguration kStreamsConfig() {
        Map<String, Object> props = Map.of(
            StreamsConfig.APPLICATION_ID_CONFIG, this.applicationId,
            StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, this.bootstrapAddress,
            StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, StringSerde.class.getName(),
            StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, JsonSerde.class.getName(),
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, this.autoOffsetReset
        );

        return new KafkaStreamsConfiguration(props);
    }
 
    @Bean
    public DefaultKafkaConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> props = Map.of(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, this.bootstrapAddress,
            ConsumerConfig.GROUP_ID_CONFIG, this.consumerGroupId,
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName(),
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName()
        );

        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
     
     @Bean
     public KStream<String, UserLogin> kStream(StreamsBuilder kStreamBuilder) {
         KStream<String, UserLogin> stream = kStreamBuilder.stream(inputTopic);
         this.kstreamProcessor.process(stream);
         return stream;
     }
}