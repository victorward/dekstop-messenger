package zaawjava.controllers;

import io.netty.channel.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import utils.MessageHandler;
import utils.MessageService;
import zaawjava.ScreensManager;
import zaawjava.services.SocketService;

import java.io.IOException;

@Component
public class LoginController {
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    static final String HOST = "localhost";
    static final int PORT = 8080;
    private ScreensManager screensManager;

    private Stage stage;

    private Channel channel;
    private EventLoopGroup group;
    private Boolean connecting = false;

    private MessageService messageService = new MessageService();

    private final SocketService socketService;

    @FXML
    private Label messageLabel;
    @FXML
    private TextField loginField;
    @FXML
    private TextField passwordField;

    @Autowired
    public LoginController(SocketService socketService) {
        this.socketService = socketService;
    }

    public void setScreensManager(ScreensManager screensManager) {
        this.screensManager = screensManager;
    }

    private void setMainView() throws IOException {
        FXMLLoader loader = new FXMLLoader();

        Parent rootNode = (Parent) loader.load(getClass().getResourceAsStream("/fxml/mainView.fxml"));
        MainViewController c = (MainViewController) loader.getController();
        c.setParameters(stage, channel, group);
        Scene scene = new Scene(rootNode);
        Platform.runLater(() -> stage.setScene(scene));
    }

    private void connect() {
        if (connecting) {
            return;
        }
//        if (channel != null && channel.isOpen()) {
//            return;
//        }
        connecting = true;
        log.debug("Trying to connect...");
        messageLabel.setText("Connecting...");
//        group = new NioEventLoopGroup();

//        Bootstrap b = new Bootstrap();
//        b.group(group)
//                .channel(NioSocketChannel.class)
//                .handler(new ChannelInitializer<SocketChannel>() {
//                    @Override
//                    public void initChannel(SocketChannel ch) throws Exception {
//                        ChannelPipeline p = ch.pipeline();
//
//                        p.addLast(
//                                new ObjectEncoder(),
//                                new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
//                                new ClientHandler(messageService));
//                    }
//                });
//
//
//        b.connect(HOST, PORT).addListener((ChannelFuture future) -> {

        socketService.connect().addListener((ChannelFuture future) -> {
            if (future.isSuccess()) {
                channel = future.channel();
                messageService.setChannel(channel);

                log.debug("Connected");
                Platform.runLater(() -> messageLabel.setText("Connected."));
                connecting = false;
                login();

            } else {
                log.debug("Connection error");

                future.channel().close();
                group.shutdownGracefully();

                Platform.runLater(() -> messageLabel.setText("Connection error"));
                connecting = false;
            }

        });

//        });
    }

    private void login() {
        User user = new User(loginField.getText(), passwordField.getText());

        messageService.sendMessage("onLogin", user, new MessageHandler() {
            @Override
            public void handle(Object msg, ChannelFuture future) {
                //TODO error handling
                log.debug("response recived: " + msg);
                try {
                    if ("loggedIn".equals((String) msg))
                        setMainView();
                } catch (IOException e) {
                    log.debug(e.getMessage());
                    Platform.runLater(() -> messageLabel.setText("Cannot load main view"));
                }
            }
        });

    }

    @FXML
    public void onLoginButton(ActionEvent event) throws IOException {
        connect();
        stage.setOnCloseRequest(event1 -> {
            log.debug("closing window...");
            if (channel != null && group != null) {
                channel.close();
                group.shutdownGracefully();
            }

        });
    }

    public void setParameters(Stage stage) {
        this.stage = stage;
    }

    private void onButton(ActionEvent event) {
        log.debug("button");
        messageService.sendMessage("event", "param", new MessageHandler() {
            @Override
            public void handle(Object resp, ChannelFuture future) {
                log.debug("response recived " + resp);

            }
        });
    }

    @FXML
    private void onRegisterButton(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();

        Parent rootNode = (Parent) loader.load(getClass().getResourceAsStream("/fxml/registration.fxml"));
        RegistrationController c = (RegistrationController) loader.getController();
        c.setParameters(channel, group);
        Scene scene = new Scene(rootNode);

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();

    }
}
