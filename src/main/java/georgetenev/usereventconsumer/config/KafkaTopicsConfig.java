package georgetenev.usereventconsumer.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicsConfig {

    @Value(value = "${spring.kafka.topic.logins-processed.name}")
    private String loginsProcessedTopic;

    @Bean
    public NewTopic topicWith3Replicas() {
        return TopicBuilder.name(loginsProcessedTopic)
            .replicas(3)
            .build();
    }

    public String getLoginsProcessedTopic() {
        return loginsProcessedTopic;
    }
}
