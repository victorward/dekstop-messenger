package zaawjava;

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
import utils.Message;
import utils.MessageHandler;
import utils.MessageService;
import zaawjava.Utils.Utils;
import zaawjava.Utils.UtilsDTO;
import zaawjava.model.Country;
import zaawjava.model.Language;
import zaawjava.model.User;
import zaawjava.services.DatabaseConnector;
import zaawjava.services.UserService;

import java.util.ArrayList;
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

    private User tmpUser;

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
                User user = UtilsDTO.convertDTOtoUser(userDTO);
                log.debug("Trying to log in! For " + user);
                User userToCheck = databaseConnector.getByEmail(user.getEmail());
                if (!userService.checkIfLogged(userToCheck)) {
                    if (checkPassword(user)) {
                        messageService.sendMessage("onLogin", "loggedIn");
                    } else {
                        messageService.sendMessage("onLogin", message);
                    }
                } else {
                    messageService.sendMessage("onLogin", "Account already connected");
                }

            }
        });

        this.messageService.registerHandler("onRegistration", new MessageHandler() {
            @Override
            public void handle(Object msg, Channel channel, ChannelFuture future) {
                message = "";
                log.debug("Registration" + msg);
                UserDTO userDTO = (UserDTO) msg;
                User user = UtilsDTO.convertDTOtoUser(userDTO);
                if (!Optional.ofNullable(checkUserInDatabase(user.getEmail())).isPresent()) {
                    if (addNewUser(user)) {
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
                ServerConnectionsHandler.this.messageService.sendMessage("getLoggedUser", UtilsDTO.convertUserToDTO(tmpUser));
                userService.addUserToLoggedList(tmpUser, channel);
                messageService.sendMessageToGroup(allChannels, "numberOfUsersChanged", userService.getNumberOfLoggedUsers());
            }
        });

        this.messageService.registerHandler("loggedOutUser", new MessageHandler() {
            @Override
            public void handle(Object msg, Channel channel, ChannelFuture future) {
                User user = UtilsDTO.convertDTOtoUser((UserDTO) msg);
                messageService.sendMessage("loggedOutUser", "loggedOutUser");
                userService.deleteUserFromLoggedList(user);
                messageService.sendMessageToGroup(allChannels, "numberOfUsersChanged", userService.getNumberOfLoggedUsers());
            }
        });

        this.messageService.registerHandler("updateUser", new MessageHandler() {
            @Override
            public void handle(Object msg, Channel channel, ChannelFuture future) {
                UserDTO userDTO = (UserDTO) msg;
                User user = UtilsDTO.convertDTOtoUser(userDTO);
                user.setPassword(Utils.encryptPassword(user.getPassword()));
                log.debug("|Przy update " + user);
                if (user != null) {
                    if (updateUser(user)) {
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
                List<Country> list = databaseConnector.getCountries();
                List<CountryDTO> listDto = new ArrayList<>();
                for (Country country : list) {
                    listDto.add(UtilsDTO.convertCountryToDTO(country));
                }
                messageService.sendMessage("getCountryList", listDto);
            }
        });

        this.messageService.registerHandler("getLanguagesList", new MessageHandler() {
            @Override
            public void handle(Object msg, Channel channel, ChannelFuture future) {
                List<Language> list = databaseConnector.getLanguages();
                List<LanguageDTO> languageDTO = new ArrayList<>();
                for (Language lang : list) {
                    languageDTO.add(UtilsDTO.convertLanguageToDTO(lang));
                }
                messageService.sendMessage("getLanguagesList", languageDTO);
            }
        });
        this.messageService.registerHandler("onNumberOfUsers", new MessageHandler() {
            @Override
            public void handle(Object msg, Channel channel, ChannelFuture future) {
                messageService.sendMessage("onNumberOfUsers", userService.getNumberOfLoggedUsers());
            }
        });
    }

    private boolean updateUser(User user) {
        try {
            databaseConnector.updateUser(user);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean checkPassword(User user) {
        if (Optional.ofNullable(checkUserInDatabase(user.getEmail())).isPresent()) {
            User checkedUser = databaseConnector.getByEmail(user.getEmail());
            checkedUser.setPassword(Utils.decryptPassword(checkedUser.getPassword()));
            if (checkedUser.getPassword().equals(user.getPassword())) {
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

    public User checkUserInDatabase(String email) {
        User user = databaseConnector.getByEmail(email);
        return user;
    }

    public boolean addNewUser(User user) {
        try {
            user.setAddress("");
            user.setPhoto("");
            user.setPassword(Utils.encryptPassword(user.getPassword()));
            log.debug("Trying add to database" + user);
            databaseConnector.insertUser(user);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
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
            log.debug("message received: " + msg);
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

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
