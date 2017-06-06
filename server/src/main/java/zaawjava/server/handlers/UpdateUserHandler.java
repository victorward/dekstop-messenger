package zaawjava.server.handlers;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import zaawjava.commons.DTO.UserDTO;
import zaawjava.commons.utils.CryptoUtils;
import zaawjava.commons.utils.MessageHandler;
import zaawjava.commons.utils.MessageService;
import zaawjava.server.services.DatabaseConnector;

@Component
public class UpdateUserHandler extends MessageHandler {
    private static final Logger log = LoggerFactory.getLogger(UpdateUserHandler.class);


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
        UserDTO userDTO = (UserDTO) msg;
        userDTO.setPassword(CryptoUtils.encryptPassword(userDTO.getPassword()));
        log.debug("|Przy update " + userDTO);
        if (userDTO != null) {
            if (updateUser(userDTO)) {
                messageService.sendMessage("updateUser", "updated");
            } else {
                message += "Ups. Updating failed";
                messageService.sendMessage("updateUser", message);
            }
        }
    }

    private boolean updateUser(UserDTO user) {
        try {
            databaseConnector.updateUser(user);
            return true;
        } catch (Exception ex) {
            log.warn("User update failed", ex);
            return false;
        }
    }
}
