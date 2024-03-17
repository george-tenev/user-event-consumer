package georgetenev.usereventconsumer.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicsConfig {

    @Value(value = "${spring.kafka.topic.transformed-logins.name}")
    private String transformedLoginsTopic;

    @Bean
    public NewTopic TransformedLoginIdTopic() {
        return TopicBuilder.name(this.transformedLoginsTopic)
                .replicas(1)
                .build();
    }

}
