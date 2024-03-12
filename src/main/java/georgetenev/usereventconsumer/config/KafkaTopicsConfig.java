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
    @Value(value = "${spring.kafka.topic.daily-active-users.name}")
    private String dailyActiveUsersTopic;

    @Bean
    public NewTopic DAUTopic() {
        return TopicBuilder.name(dailyActiveUsersTopic)
            .replicas(1)
            .build();
    }

    public String getDailyActiveUsersTopic() {
        return dailyActiveUsersTopic;
    }
}
