package georgetenev.usereventconsumer.config;

import georgetenev.usereventconsumer.dto.UserLogin;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
public final class SerDes {
    private SerDes() {}
    public static Serde<UserLogin> UserLogin() {
        JsonSerializer<UserLogin> serializer = new JsonSerializer<>();
        JsonDeserializer<UserLogin> deserializer = new JsonDeserializer<>(UserLogin.class);
        return Serdes.serdeFrom(serializer, deserializer);
    }

}