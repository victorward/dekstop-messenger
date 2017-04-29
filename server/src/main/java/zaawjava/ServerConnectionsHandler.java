package zaawjava;

import DTO.UserDTO;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
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
import zaawjava.model.User;
import zaawjava.services.DatabaseConnector;
import zaawjava.services.UserService;

import java.util.Optional;

@Component
@ChannelHandler.Sharable
public class ServerConnectionsHandler extends ChannelInboundHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(ServerConnectionsHandler.class);

    private DefaultChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

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
            public void handle(Object msg, ChannelFuture future) {
                message = "";
                UserDTO userDTO = (UserDTO) msg;
                User user = UtilsDTO.convertDTOtoUser(userDTO);
                System.out.println(userDTO);
                log.debug("Trying to log in! " + user);
                if (checkPassword(user)) {
                    ServerConnectionsHandler.this.messageService.sendMessage("onLogin", "loggedIn");
                } else {
                    ServerConnectionsHandler.this.messageService.sendMessage("onLogin", message);
                }
            }
        });

        this.messageService.registerHandler("onRegistration", new MessageHandler() {
            @Override
            public void handle(Object msg, ChannelFuture future) {
                message = "";
                log.debug("Registration" + msg);
                User user = (User) msg;
                if (!Optional.ofNullable(checkUserInDatabase(user.getEmail())).isPresent()) {
                    if (addNewUser(user)) {
                        ServerConnectionsHandler.this.messageService.sendMessage("onRegistration", "registered");
                    } else {
                        ServerConnectionsHandler.this.messageService.sendMessage("onRegistration", message);
                    }
                } else {
                    message += "Already registered";
                    ServerConnectionsHandler.this.messageService.sendMessage("onRegistration", message);
                }
            }
        });

        this.messageService.registerHandler("getLoggedUser", new MessageHandler() {
            @Override
            public void handle(Object msg, ChannelFuture future) {
                ServerConnectionsHandler.this.messageService.sendMessage("getLoggedUser", UtilsDTO.convertUserToDTO(tmpUser));
                userService.addUserToLoggedList(tmpUser);
                userService.printUserList();
            }
        });

        this.messageService.registerHandler("loggedOutUser", new MessageHandler() {
            @Override
            public void handle(Object msg, ChannelFuture future) {
                User user = (User) msg;
                ServerConnectionsHandler.this.messageService.sendMessage("loggedOutUser", "loggedOutUser");
                userService.deleteUserFromLoggedList(user);
                userService.printUserList();
            }
        });

    }

    public boolean checkPassword(User user) {
        if (Optional.ofNullable(checkUserInDatabase(user.getEmail())).isPresent()) {
            User checkedUser = databaseConnector.getByEmail(user.getEmail());
            checkedUser.setPassword(Utils.decryptPassword(checkedUser.getPassword()));
            System.out.println(checkedUser.getPassword());
            log.debug("logining! " + checkedUser);
            if (checkedUser.getPassword().equals(user.getPassword())) {
                tmpUser = checkedUser;
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
        User user = null;
        user = databaseConnector.getByEmail(email);
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
        channels.add(ctx.channel());
        log.debug("Channel Active");

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            log.debug("message recived: " + msg);
            messageService.setChannel(ctx.channel());
            messageService.handleMessage((Message) msg);

        } catch (ClassCastException e) {
            log.warn("Not message recived");
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
