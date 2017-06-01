package zaawjava.server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import zaawjava.commons.utils.MessageService;

@Configuration
@ComponentScan
public class AppConfig {
    @Bean
    public MessageService messageService() {
        return new MessageService();
    }
}