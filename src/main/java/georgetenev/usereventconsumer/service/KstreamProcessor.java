package georgetenev.usereventconsumer.service;

import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import georgetenev.usereventconsumer.config.KafkaTopicsConfig;
import georgetenev.usereventconsumer.dto.UserLogin; 

@Component
public class KstreamProcessor {
 
    @Autowired
    private KafkaTopicsConfig kafkaTopicsConfig;

    public void process(KStream<String, UserLogin> stream){

        stream.to(kafkaTopicsConfig.getLoginsProcessedTopic());  

    }
}