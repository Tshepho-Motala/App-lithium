package lithium.service.event.client.stream;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBinding(EventOutputQueue.class)
@ComponentScan
public class EventStreamConfiguration {
}
