package zaawjava.server.handlers;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import zaawjava.commons.DTO.UserDTO;
import zaawjava.commons.utils.MessageHandler;
import zaawjava.commons.utils.MessageService;
import zaawjava.server.services.DatabaseConnector;

import java.util.Optional;

@Component
public class RegistrationHandler extends MessageHandler {
    private static final Logger log = LoggerFactory.getLogger(RegistrationHandler.class);

    private MessageService messageService;
    private DatabaseConnector databaseConnector;

    @Autowired
    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    @Autowired
    public void setDatabaseConnector(DatabaseConnector databaseConnector) {
        this.databaseConnector = databaseConnector;
    }

    @Override
    public void handle(Object msg, Channel channel, ChannelFuture future) {
        String message = "";
        log.debug("Registration" + msg);
        UserDTO userDTO = (UserDTO) msg;
        if (!Optional.ofNullable(databaseConnector.getUserByEmail(userDTO.getEmail())).isPresent()) {
            if (databaseConnector.addNewUser(userDTO)) {
                messageService.sendMessage("onRegistration", "registered");
            } else {
                messageService.sendMessage("onRegistration", message);
            }
        } else {
            message += "Already registered";
            messageService.sendMessage("onRegistration", message);
        }
    }
}
