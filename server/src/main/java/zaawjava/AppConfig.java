package zaawjava;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import utils.MessageService;

@Configuration
@ComponentScan
public class AppConfig {
    @Bean
    public MessageService messageService() {
        return new MessageService();
    }
}