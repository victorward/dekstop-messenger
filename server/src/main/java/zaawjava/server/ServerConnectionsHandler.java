package zaawjava.server;

import io.netty.channel.*;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import zaawjava.commons.DTO.ChatMessageDTO;
import zaawjava.commons.DTO.CountryDTO;
import zaawjava.commons.DTO.LanguageDTO;
import zaawjava.commons.DTO.UserDTO;
import zaawjava.commons.utils.CryptoUtils;
import zaawjava.commons.utils.Message;
import zaawjava.commons.utils.MessageHandler;
import zaawjava.commons.utils.MessageService;
import zaawjava.server.Utils.DTOUtils;
import zaawjava.server.handlers.RegistrationHandler;
import zaawjava.server.handlers.UserLoggedOutHandler;
import zaawjava.server.model.ChatMessage;
import zaawjava.server.model.Conversation;
import zaawjava.server.services.DatabaseConnector;
import zaawjava.server.services.UserService;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

@Component
@ChannelHandler.Sharable
public class ServerConnectionsHandler extends ChannelInboundHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(ServerConnectionsHandler.class);

    private final MessageService messageService;

    private DatabaseConnector databaseConnector;

    private String message;

    private UserDTO tmpUser;

    private UserService userService;

    //handlers
    private RegistrationHandler registrationHandler;
    private UserLoggedOutHandler userLoggedOutHandler;


    @Autowired
    public void setRegistrationHandler(RegistrationHandler registrationHandler) {
        this.registrationHandler = registrationHandler;
    }

    @Autowired
    public void setUserLoggedOutHandler(UserLoggedOutHandler userLoggedOutHandler) {
        this.userLoggedOutHandler = userLoggedOutHandler;
    }

    @Autowired
    public void setDatabaseConnector(DatabaseConnector databaseConnector) {
        this.databaseConnector = databaseConnector;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public ServerConnectionsHandler(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostConstruct
    private void init() {
        this.messageService.registerHandler("onLogin", new MessageHandler() {
            @Override
            public void handle(Object msg, Channel channel, ChannelFuture future) {
                message = "";
                UserDTO userDTO = (UserDTO) msg;
                log.debug("Trying to log in! For " + userDTO);
                UserDTO userToCheck = databaseConnector.getUserByEmail(userDTO.getEmail());
                if (!userService.checkIfLogged(userToCheck)) {
                    if (checkPassword(userDTO)) {
                        messageService.sendMessage("onLogin", "loggedIn");
                    } else {
                        messageService.sendMessage("onLogin", message);
                    }
                } else {
                    messageService.sendMessage("onLogin", "Account already connected or wrong email");
                }

            }
        });

        this.messageService.registerHandler("onRegistration", registrationHandler);

        this.messageService.registerHandler("getLoggedUser", new MessageHandler() {
            @Override
            public void handle(Object msg, Channel channel, ChannelFuture future) {
                ServerConnectionsHandler.this.messageService.sendMessage("getLoggedUser", tmpUser);
                userService.addUserToLoggedList(tmpUser, channel);
                messageService.sendMessageToGroup(userService.getAllChannels(), "numberOfUsersChanged", userService.getNumberOfLoggedUsers());
                messageService.sendMessageToGroup(userService.getAllChannels(), "listOfUsersChanged", userService.getMapOfUsersWithStatus());
            }
        });

        this.messageService.registerHandler("loggedOutUser", userLoggedOutHandler);

        this.messageService.registerHandler("updateUser", new MessageHandler() {
            @Override
            public void handle(Object msg, Channel channel, ChannelFuture future) {
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
        });

        this.messageService.registerHandler("getCountryList", new MessageHandler() {
            @Override
            public void handle(Object msg, Channel channel, ChannelFuture future) {
                List<CountryDTO> list = databaseConnector.getCountries();
                messageService.sendMessage("getCountryList", list);
            }
        });

        this.messageService.registerHandler("getLanguagesList", new MessageHandler() {
            @Override
            public void handle(Object msg, Channel channel, ChannelFuture future) {
                List<LanguageDTO> list = databaseConnector.getLanguages();
                messageService.sendMessage("getLanguagesList", list);
            }
        });
        this.messageService.registerHandler("onNumberOfUsers", new MessageHandler() {
            @Override
            public void handle(Object msg, Channel channel, ChannelFuture future) {
                messageService.sendMessage("onNumberOfUsers", userService.getNumberOfLoggedUsers());
            }
        });
        this.messageService.registerHandler("checkUserList", new MessageHandler() {
            @Override
            public void handle(Object msg, Channel channel, ChannelFuture future) {
                messageService.sendMessage("checkUserList", getAllUserList());
            }
        });
        this.messageService.registerHandler("getUsersStatus", new MessageHandler() {
            @Override
            public void handle(Object msg, Channel channel, ChannelFuture future) {
                messageService.sendMessage("getUsersStatus", userService.getMapOfUsersWithStatus());
            }
        });

        this.messageService.registerHandler("newGlobalChatMessage", new MessageHandler() {
            @Override
            public void handle(Object msg, Channel channel, ChannelFuture future) {
                messageService.sendMessageToGroup(userService.getAllChannels(), "onGlobalChatMessage", msg);
            }
        });

        this.messageService.registerHandler("getConversation", new MessageHandler() {
            @Override
            public void handle(Object msg, Channel channel, ChannelFuture future) {
                UserDTO user = (UserDTO) msg;
                List<ChatMessageDTO> messageList =
                        databaseConnector.getMessageListByUsers(userService.getUserByChannel(channel), user);
                messageService.sendMessage("getConversation", messageList);
            }
        });
        this.messageService.registerHandler("newPrivateMessage", new MessageHandler() {
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
        });

    }


    private List<UserDTO> getAllUserList() {
        List<UserDTO> allUsers = databaseConnector.getAllUsers();
        return allUsers;
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

    public boolean checkPassword(UserDTO user) {
        if (Optional.ofNullable(checkUserInDatabase(user.getEmail())).isPresent()) {
            UserDTO checkedUser = databaseConnector.getUserByEmail(user.getEmail());
            checkedUser.setPassword(CryptoUtils.decryptPassword(checkedUser.getPassword()));
            if (checkedUser.getPassword().equals(CryptoUtils.decryptPassword(user.getPassword()))) {
                tmpUser = checkedUser;
                log.debug("Logged! Actual system " + tmpUser);
                return true;
            } else {
                message += "Wrong password";
                return false;
            }
        } else {
            message += "This email doesn't exist";
            return false;
        }
    }

    public UserDTO checkUserInDatabase(String email) {
        UserDTO user = databaseConnector.getUserByEmail(email);
        return user;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug("Channel Active");

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            log.debug("message received: " + (msg != null ? msg.toString() : null));
            messageService.setChannel(ctx.channel());
            messageService.handleMessage((Message) msg);

        } catch (ClassCastException e) {
            log.warn("Not message received" + msg);
        }

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        log.debug("read complete");
        ctx.flush();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //disconnected
        log.debug("channel inactive");
        userService.deleteUserFromLoggedList(ctx.channel());

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.warn("Netty connection exception", cause);
        userService.deleteUserFromLoggedList(ctx.channel());
        ctx.close();
    }

}
