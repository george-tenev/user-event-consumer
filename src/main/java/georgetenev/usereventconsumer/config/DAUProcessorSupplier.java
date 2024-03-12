package georgetenev.usereventconsumer.config;

import georgetenev.usereventconsumer.dto.UserLogin;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.kafka.streams.state.KeyValueStore;

import org.apache.kafka.streams.processor.api.ProcessorSupplier;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

class DAUProcessorSupplier implements ProcessorSupplier<String, UserLogin, String, String> {
    private final String storeName;

    public DAUProcessorSupplier(String storeName) {
        this.storeName = storeName;
    }

    @Override
    public Processor<String, UserLogin, String, String> get() {
        return new Processor<>() {
            private ProcessorContext<String, String> context;
            private KeyValueStore<String, String> stateStore;

            @Override
            public void init(ProcessorContext<String, String> context) {
                this.context = context;
                this.stateStore = context.getStateStore(storeName);
            }

            @Override
            public void process(Record<String, UserLogin> record) {
                long timestamp = record.timestamp();
                String key = record.key();
                LocalDate date = Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDate();
                String compositeKey = key + "_" + date.toString();

                if (stateStore.get(compositeKey) == null) {
                    stateStore.put(compositeKey, "processed");
                    context.forward(new Record<>(compositeKey, key, record.timestamp()));
                }

                System.out.println("Processed incoming record - key " + key + " value " + record.value());
            }
        };
    }
}