package georgetenev.usereventconsumer.config;

import java.time.Duration;
import java.util.Date;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;

import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;

import org.apache.kafka.streams.state.Stores;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;

import georgetenev.usereventconsumer.dto.UserLogin;

import org.apache.kafka.common.protocol.types.Field.Str;
import org.apache.kafka.common.serialization.Serdes;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@EnableKafka
@EnableKafkaStreams
@Configuration
public class KafkaStreamsConfig {

    final static String STATE_STORE = "user-activity-store"; 
    final static String DAU_PROCESSOR = "dau-processor";
    final static String DAU_TOPIC = "daily-active-users";
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Value("${spring.kafka.topic.logins.name}")
    private String loginsTopic;

    @Value("${spring.kafka.consumer.auto-offset-reset}")
    private String autoOffsetReset;

    @Value("${spring.kafka.streams.application-id}")
    private String applicationId;



    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    public KafkaStreamsConfiguration kStreamsConfig() {

        Map<String, Object> props = Map.of(
            StreamsConfig.APPLICATION_ID_CONFIG, this.applicationId,
            StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, this.bootstrapAddress,
            StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName(),
            StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName(),
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, this.autoOffsetReset,
            JsonDeserializer.TRUSTED_PACKAGES, "*"
        );

        return new KafkaStreamsConfiguration(props);
    }

    @Bean
    public KStream<String, UserLogin> produceDailyActiveUsers(StreamsBuilder builder){
        builder.addStateStore(Stores.keyValueStoreBuilder(
                Stores.persistentKeyValueStore(STATE_STORE),
                Serdes.String(),
                Serdes.String()
                )
        );
        KStream<String, UserLogin> loginsStream = builder
                .stream(loginsTopic, Consumed.with(Serdes.String(), SerDes.UserLogin()));
        loginsStream.process(
                new DAUProcessorSupplier(STATE_STORE),
                        Named.as(DAU_PROCESSOR), STATE_STORE)
                .to(DAU_TOPIC, Produced.with(Serdes.String(), Serdes.String()));

        return loginsStream;
    }
}