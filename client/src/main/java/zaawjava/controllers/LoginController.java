package zaawjava.controllers;

import DTO.UserDTO;
import io.netty.channel.ChannelFuture;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import zaawjava.ScreensManager;
import zaawjava.services.SocketService;
import zaawjava.services.UserService;
import zaawjava.utils.Utils;

import java.io.IOException;

@Component
public class LoginController {
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private final SocketService socketService;
    private ScreensManager screensManager;
    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @FXML
    private Label messageLabel;
    @FXML
    private TextField loginField;
    @FXML
    private TextField passwordField;

    @FXML
    private Button login;
    @FXML
    private Button registration;

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

    @FXML
    public void initialize() {
        loginField.setText("ii@i.ua");
        passwordField.setText("pass");
    }

    private void login() {
        UserDTO user = new UserDTO(loginField.getText(), passwordField.getText());
        if (isInputValid()) {
            socketService.emit("onLogin", user).whenComplete((msg, ex) -> {
                if (ex == null) {
                    try {
                        if ("loggedIn".equals(msg)) {
                            getLoggedUser();
                            setMainView();
                        } else {
                            Platform.runLater(() -> messageLabel.setText("Login failed. " + msg));
                        }
                    } catch (IOException e) {
                        Platform.runLater(() -> messageLabel.setText("Cannot load main view"));
                    }
                } else {
                    Platform.runLater(() -> messageLabel.setText("Login failed during checking data"));
                }
            });
        } else {
            Platform.runLater(() -> messageLabel.setText("Login failed. Please write correct values"));
        }
    }

    private void getLoggedUser() {
        socketService.emit("getLoggedUser", "").whenComplete((msg, ex) -> {
            if (ex == null) {
                userService.setUser((UserDTO) msg);
                log.debug("|Ustawil sie taki user: " + userService.getUser());
            } else {
                Platform.runLater(() -> messageLabel.setText("Failed during setting actual user"));
            }
        });
    }

    private boolean isInputValid() {
        String errorMessage = "";

        if (loginField.getText() == null || loginField.getText().length() == 0) {
            errorMessage += "Empty email!\n";
        } else {
            if (!Utils.validateEmail(loginField.getText())) {
                errorMessage += "Not correct email!\n";
            }
        }
        if (passwordField.getText() == null || passwordField.getText().length() == 0) {
            errorMessage += "Empty password!\n";
        }

        if (errorMessage.length() == 0) {
            return true;
        } else {
            Alert alert = new Alert(AlertType.ERROR);
            alert.initOwner(screensManager.getStage());
            alert.setTitle("Invalid Fields");
            alert.setHeaderText("Please correct invalid fields");
            alert.setContentText(errorMessage);
            alert.showAndWait();
            return false;
        }
    }

    @FXML
    public void onLoginButton(ActionEvent event) throws IOException {

        if (!socketService.isConnected()) {
            socketService.connect().addListener((ChannelFuture future) -> {
                if (future.isSuccess()) {
                    log.debug("Connected with server");
                    Platform.runLater(() -> getMessageLabel().setText("Connected."));
                    login();
                } else {
                    log.debug("Connection error. Please check server configuration");
                    Platform.runLater(() -> getMessageLabel().setText("Connection error. Please check server configuration"));
                    Platform.runLater(() -> getLogin().setDisable(true));
                    Platform.runLater(() -> getRegistration().setDisable(true));
                }
            });
        }

    }

    @FXML
    private void onRegisterButton(ActionEvent event) throws IOException {
        screensManager.goToRegistrationView();
    }

    public Label getMessageLabel() {
        return messageLabel;
    }

    public Button getLogin() {
        return login;
    }

    public Button getRegistration() {
        return registration;
    }
}
