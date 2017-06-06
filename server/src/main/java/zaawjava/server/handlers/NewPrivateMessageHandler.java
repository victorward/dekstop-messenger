package zaawjava.server.handlers;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import zaawjava.commons.DTO.ChatMessageDTO;
import zaawjava.commons.DTO.UserDTO;
import zaawjava.commons.utils.MessageHandler;
import zaawjava.commons.utils.MessageService;
import zaawjava.server.Utils.DTOUtils;
import zaawjava.server.model.ChatMessage;
import zaawjava.server.model.Conversation;
import zaawjava.server.services.DatabaseConnector;
import zaawjava.server.services.UserService;

@Component
public class NewPrivateMessageHandler extends MessageHandler {
    private MessageService messageService;
    private DatabaseConnector databaseConnector;
    private UserService userService;

    @Autowired
    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    @Autowired
    public void setDatabaseConnector(DatabaseConnector databaseConnector) {
        this.databaseConnector = databaseConnector;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void handle(Object msg, Channel channel, ChannelFuture future) {
        ChatMessageDTO chatMessageDTO = (ChatMessageDTO) msg;
        UserDTO sender = userService.getUserByChannel(channel);
        UserDTO recipient = chatMessageDTO.getRecipient();

        Conversation conversation = databaseConnector.getConversationByUsers(sender, recipient);
        ChatMessage newChatMessage = DTOUtils.convertDTOToChatMessage(chatMessageDTO);
        newChatMessage.setConversation(conversation);

        databaseConnector.insertPrivateMessage(newChatMessage);

        if (userService.checkIfLogged(recipient)) {
            DefaultChannelGroup users = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
            users.add(channel);
            users.add(userService.getUserChannel(recipient));

            messageService.sendMessageToGroup(users, "privateMessage", msg);
        } else {
            messageService.sendMessage("privateMessage", msg);
        }
    }
}
