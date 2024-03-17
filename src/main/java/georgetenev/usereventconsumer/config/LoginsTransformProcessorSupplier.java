package georgetenev.usereventconsumer.config;

import georgetenev.usereventconsumer.dto.UserLogin;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;

import org.apache.kafka.streams.processor.api.ProcessorSupplier;
class LoginsTransformProcessorSupplier implements ProcessorSupplier<String, UserLogin, String, UserLogin> {

    @Override
    public Processor<String, UserLogin, String, UserLogin> get() {
        return new Processor<>() {
            private ProcessorContext<String, UserLogin> context;

            @Override
            public void init(ProcessorContext<String, UserLogin> context) {
                this.context = context;
            }

            @Override
            public void process(Record<String, UserLogin> record) {
//                UserLogin userLoginToForward = record.value();
//                userLoginToForward.setTimestamp(record.timestamp());
//                Record<String, UserLogin> toForward = new Record<>(record.key(), user
                UserLogin userLoginToForward = new UserLogin(record.value());
                userLoginToForward.setTimestamp(record.timestamp());
                Record<String, UserLogin> toForward = new Record<>(record.key(), userLoginToForward, record.timestamp());
                System.out.println("Test 1");
                context.forward(toForward);
                System.out.println("Processed incoming record - key " + record.key() + " value " + record.timestamp());
            }
        };
    }
}