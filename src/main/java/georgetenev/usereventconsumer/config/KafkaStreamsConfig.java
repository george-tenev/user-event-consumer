package georgetenev.usereventconsumer.config;

import java.time.Duration;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;

import georgetenev.usereventconsumer.dto.UserLogin;
import org.apache.kafka.streams.processor.api.ProcessorSupplier;
import org.apache.kafka.common.protocol.types.Field.Str;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@EnableKafka
@Configuration
public class KafkaStreamsConfig {

    final static String TRUSTED_PACKAGES = "*";
    final static String LOGINS_SOURCE_TOPIC_NAME = "LoginsSourceTopic";
    final static String LOGINS_TRANSFORM_PROCESSOR_NAME = "LoginsTransformNode";
    final static String LOGINS_TRANSFORMED_TOPIC_NAME = "LoginsTransformedTopic";
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Value("${spring.kafka.topic.logins.name}")
    private String loginsTopic;

    @Value("${spring.kafka.topic.transformed-logins.name}")
    private String transformedLoginsTopic;
    @Value("${spring.kafka.consumer.auto-offset-reset}")
    private String autoOffsetReset;

    @Value("${spring.kafka.streams.application-id}")
    private String applicationId;

    Map <String, Object> getStreamsProerties() {
        return Map.of(
                StreamsConfig.APPLICATION_ID_CONFIG, this.applicationId,
                StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, this.bootstrapAddress,
                StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName(),
                StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, SerDe.class.getName(),
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, this.autoOffsetReset,
                JsonDeserializer.TRUSTED_PACKAGES, TRUSTED_PACKAGES
        );
    }

    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    public KafkaStreamsConfiguration kStreamsConfig() {
        Map<String, Object> props = getStreamsProerties();
        return new KafkaStreamsConfiguration(props);
    }

    @Bean
    @Qualifier("LoginsTransformedTopic")
    public KafkaStreams transformedKStream() {
        Properties properties = new Properties();
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, this.applicationId);
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, this.bootstrapAddress);
        properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, SerDe.class.getName());
        properties.put(JsonDeserializer.TRUSTED_PACKAGES, TRUSTED_PACKAGES);

        Serde<UserLogin> userLoginSerde = new SerDe();

        StreamsBuilder builder = new StreamsBuilder();
        Topology topology = builder.build();
        topology
                .addSource(LOGINS_SOURCE_TOPIC_NAME, loginsTopic)
                .addProcessor(
                        LOGINS_TRANSFORM_PROCESSOR_NAME,
                        new LoginsTransformProcessorSupplier(),
                        LOGINS_SOURCE_TOPIC_NAME
                ).addSink(
                        LOGINS_TRANSFORMED_TOPIC_NAME,
                        transformedLoginsTopic,
                        Serdes.String().serializer(),
                        userLoginSerde.serializer(),
                        LOGINS_TRANSFORM_PROCESSOR_NAME
                );

        Map<String, Object> props = getStreamsProerties();

        final KafkaStreams kafkaStreams = new KafkaStreams(topology, properties);
        kafkaStreams.start();
        Runtime.getRuntime().addShutdownHook(new Thread(kafkaStreams::close));
        return kafkaStreams;
    }

}