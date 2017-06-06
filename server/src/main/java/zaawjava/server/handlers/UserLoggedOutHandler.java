package zaawjava.server.handlers;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import zaawjava.commons.DTO.UserDTO;
import zaawjava.commons.utils.MessageHandler;
import zaawjava.commons.utils.MessageService;
import zaawjava.server.services.UserService;

@Component
public class UserLoggedOutHandler extends MessageHandler {
    private MessageService messageService;
    private UserService userService;

    @Autowired
    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void handle(Object msg, Channel channel, ChannelFuture future) {
        UserDTO user = (UserDTO) msg;
        messageService.sendMessage("loggedOutUser", "loggedOutUser");
        userService.deleteUserFromLoggedList(user);
        messageService.sendMessageToGroup(userService.getAllChannels(), "numberOfUsersChanged", userService.getNumberOfLoggedUsers());
        messageService.sendMessageToGroup(userService.getAllChannels(), "listOfUsersChanged", userService.getMapOfUsersWithStatus());
    }
}
