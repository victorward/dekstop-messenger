package zaawjava.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import zaawjava.commons.utils.MessageService;

@Configuration
@ComponentScan
public class SpringApplicationConfig {
    @Bean
    public MessageService messageService() {
        return new MessageService();
    }
}
