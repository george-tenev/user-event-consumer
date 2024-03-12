package georgetenev.usereventconsumer.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class KafkaTopicListener {

    @KafkaListener(topics = "${spring.kafka.topic.logins.name}")
    public void readRxClaimStream(@Payload String record) {
        System.out.println("LOGINS => " + record);
    }

    @KafkaListener(topics = "daily-active-users")
    public void DAU(@Payload String record) {
        System.out.println("DAU => " + record);
    }

}