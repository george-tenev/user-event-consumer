package georgetenev.usereventconsumer.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.io.IOException;

public class JsonSerde<T> implements Serde<T> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Serializer<T> serializer() {
        return (topic, data) -> {
            try {
                return objectMapper.writeValueAsBytes(data);
            } catch (JsonProcessingException e) {
                throw new SerializationException("Error serializing JSON message", e);
            }
        };
    }

    @Override
    public Deserializer<T> deserializer() {
        return (topic, data) -> {
            try {
                return objectMapper.readValue(data, new TypeReference<T>() {});
            } catch (IOException e) {
                throw new SerializationException("Error deserializing JSON message", e);
            }
        };
    }
}