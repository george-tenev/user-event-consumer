package georgetenev.usereventconsumer.config;

import georgetenev.usereventconsumer.dto.UserLogin;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
public class SerDe implements Serde<UserLogin> {

    public SerDe() {
    }

    @Override
    public Serializer<UserLogin> serializer() {
        return new JsonSerializer<>();
    }

    @Override
    public Deserializer<UserLogin> deserializer() {
        return new JsonDeserializer<>(UserLogin.class);
    }
}
