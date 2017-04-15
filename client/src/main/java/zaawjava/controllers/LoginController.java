package zaawjava.controllers;

import io.netty.channel.ChannelFuture;
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
import org.springframework.stereotype.Component;
import zaawjava.ScreensManager;
import zaawjava.services.SocketService;

import java.io.IOException;

@Component
public class LoginController {
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private Boolean connecting = false;

    private final SocketService socketService;
    private ScreensManager screensManager;


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

    @Autowired
    public void setScreensManager(ScreensManager screensManager) {
        this.screensManager = screensManager;
    }

    private void setMainView() throws IOException {
        Platform.runLater(() -> screensManager.goToMainView());
    }

    private void connect() {
        if (connecting) {
            return;
        }
        connecting = true;
        log.debug("Trying to connect...");
        messageLabel.setText("Connecting...");

        socketService.connect().addListener((ChannelFuture future) -> {
            if (future.isSuccess()) {

                log.debug("Connected");
                Platform.runLater(() -> messageLabel.setText("Connected."));
                connecting = false;
                login();

            } else {
                log.debug("Connection error");

                Platform.runLater(() -> messageLabel.setText("Connection error"));
                connecting = false;
            }

        });

//        });
    }

    private void login() {
        User user = new User(loginField.getText(), passwordField.getText());

        socketService.emit("onLogin", user).whenComplete((msg, ex) -> {
            if (ex == null) {
                try {
                    if ("loggedIn".equals(msg))
                        setMainView();
                } catch (IOException e) {
                    Platform.runLater(() -> messageLabel.setText("Cannot load main view"));
                }
            } else {
                Platform.runLater(() -> messageLabel.setText("Login failed"));

            }
        });

    }

    @FXML
    public void onLoginButton(ActionEvent event) throws IOException {
        connect();
    }

    @FXML
    private void onRegisterButton(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();

        Parent rootNode = (Parent) loader.load(getClass().getResourceAsStream("/fxml/registration.fxml"));
        RegistrationController c = (RegistrationController) loader.getController();
        Scene scene = new Scene(rootNode);

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();

    }
}
