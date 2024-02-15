package georgetenev.usereventconsumer.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import georgetenev.usereventconsumer.dto.UserLogin;
import georgetenev.usereventconsumer.service.KstreamProcessor ;
import static org.apache.kafka.streams.StreamsConfig.*;

@EnableKafka
@EnableKafkaStreams
@Configuration
public class KafkaStreamsConfig {
 
 @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;
 
 @Value(value = "${spring.kafka.topic.logins.name}")
    private String inputTopic;

  @Value(value = "${spring.kafka.consumer.group-id}")
    private String consumerGroupId;

  @Value(value = "${spring.kafka.consumer.auto-offset-reset}")
    private String autoOffsetReset;
  
 
 @Autowired
 private KstreamProcessor kstreamProcessor;


@Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
 public KafkaStreamsConfiguration kStreamsConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-app");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, JsonSerde.class);
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, JsonSerde.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        return new KafkaStreamsConfiguration(props);
  }
 
  @Bean
  public ConsumerFactory<String, String> consumerFactory() {
        Map props = new HashMap();
            props.put("bootstrap.servers", "localhost:29092");
         props.put(
           ConsumerConfig.GROUP_ID_CONFIG, 
           "demo-1");
         props.put(
           ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, 
           StringDeserializer.class);
         props.put(
           ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, 
           StringDeserializer.class);
         return new DefaultKafkaConsumerFactory<>(props);
     }

     @Bean
     public ConcurrentKafkaListenerContainerFactory<String, String> 
       kafkaListenerContainerFactory() {
    
         ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
         factory.setConsumerFactory(consumerFactory());
         return factory;
     }
     
     @Bean
     public KStream<String, UserLogin> kStream(StreamsBuilder kStreamBuilder) {
      
         KStream<String, UserLogin> stream = kStreamBuilder.stream(inputTopic);
         System.out.println("KStream => " + stream);
         //Process KStream
         this.kstreamProcessor.process(stream);
         return stream;
     }
}