package zaawjava;

import DTO.ChatMessageDTO;
import DTO.CountryDTO;
import DTO.LanguageDTO;
import DTO.UserDTO;
import io.netty.channel.*;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import utils.CryptoUtils;
import utils.Message;
import utils.MessageHandler;
import utils.MessageService;
import zaawjava.Utils.DTOUtils;
import zaawjava.model.ChatMessage;
import zaawjava.model.Conversation;
import zaawjava.services.DatabaseConnector;
import zaawjava.services.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Component
@ChannelHandler.Sharable
public class ServerConnectionsHandler extends ChannelInboundHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(ServerConnectionsHandler.class);

    private DefaultChannelGroup allChannels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private final MessageService messageService;

    private DatabaseConnector databaseConnector;

    private String message;

    private UserDTO tmpUser;

    private UserService userService;

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

        this.messageService.registerHandler("onRegistration", new MessageHandler() {
            @Override
            public void handle(Object msg, Channel channel, ChannelFuture future) {
                message = "";
                log.debug("Registration" + msg);
                UserDTO userDTO = (UserDTO) msg;
                if (!Optional.ofNullable(checkUserInDatabase(userDTO.getEmail())).isPresent()) {
                    if (addNewUser(userDTO)) {
                        messageService.sendMessage("onRegistration", "registered");
                    } else {
                        messageService.sendMessage("onRegistration", message);
                    }
                } else {
                    message += "Already registered";
                    messageService.sendMessage("onRegistration", message);
                }
            }
        });

        this.messageService.registerHandler("getLoggedUser", new MessageHandler() {
            @Override
            public void handle(Object msg, Channel channel, ChannelFuture future) {
                ServerConnectionsHandler.this.messageService.sendMessage("getLoggedUser", tmpUser);
                userService.addUserToLoggedList(tmpUser, channel);
                messageService.sendMessageToGroup(allChannels, "numberOfUsersChanged", userService.getNumberOfLoggedUsers());
                messageService.sendMessageToGroup(allChannels, "listOfUsersChanged", getMapOfUsersWithStatus());
            }
        });

        this.messageService.registerHandler("loggedOutUser", new MessageHandler() {
            @Override
            public void handle(Object msg, Channel channel, ChannelFuture future) {
                UserDTO user = (UserDTO) msg;
                messageService.sendMessage("loggedOutUser", "loggedOutUser");
                userService.deleteUserFromLoggedList(user);
                messageService.sendMessageToGroup(allChannels, "numberOfUsersChanged", userService.getNumberOfLoggedUsers());
                messageService.sendMessageToGroup(allChannels, "listOfUsersChanged", getMapOfUsersWithStatus());
            }
        });

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
                messageService.sendMessage("getUsersStatus", getMapOfUsersWithStatus());
            }
        });

        this.messageService.registerHandler("newGlobalChatMessage", new MessageHandler() {
            @Override
            public void handle(Object msg, Channel channel, ChannelFuture future) {
                messageService.sendMessageToGroup(allChannels, "onGlobalChatMessage", msg);
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

    private HashMap<UserDTO, Boolean> getMapOfUsersWithStatus() {
        HashMap<UserDTO, Boolean> userList = new HashMap<>();
        List<UserDTO> activeUsers = userService.getListOfLoggedUsers();
        List<UserDTO> allUsers = databaseConnector.getAllUsers();
        for (UserDTO user : allUsers) {
            Boolean status = false;
            for (UserDTO userAct : activeUsers) {
                if (userAct.getId() == user.getId()) status = true;
            }
            userList.put(user, status);
        }
        return userList;
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

    public boolean addNewUser(UserDTO user) {
        try {
            if (user.getAddress() == null || user.getAddress().length() < 1)
                user.setAddress("");
            if (user.getPhoto() == null || user.getPhoto().length() < 1)
                user.setPhoto("");
            user.setPassword(CryptoUtils.encryptPassword(user.getPassword()));
            log.debug("Trying add to database" + user);
            databaseConnector.insertUser(user);
            return true;
        } catch (Exception ex) {
            log.warn("Adding user failed", ex);
            return false;
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        allChannels.add(ctx.channel());
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
