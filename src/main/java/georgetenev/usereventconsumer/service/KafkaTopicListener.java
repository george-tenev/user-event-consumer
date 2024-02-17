package georgetenev.usereventconsumer.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class KafkaTopicListener {

    @KafkaListener(topics = "${spring.kafka.topic.logins-processed.name}")
    public void readRxClaimStream(@Payload String record) {
        System.out.println("POCESSSED_LOGINS => " + record);
    }

}