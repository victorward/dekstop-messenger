package zaawjava.controllers;

import io.netty.channel.ChannelFuture;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
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
import javafx.scene.control.Alert.AlertType;

import java.io.IOException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private void login() {
        User user = new User(loginField.getText(), passwordField.getText());
        if (isInputValid()) {
            socketService.emit("onLogin", user).whenComplete((msg, ex) -> {
                if (ex == null) {
                    try {
                        if ("loggedIn".equals(msg)) {
                            setMainView();
                        } else {
                            Platform.runLater(() -> messageLabel.setText("Login failed"));
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

    private boolean isInputValid() {
        String errorMessage = "";

        if (loginField.getText() == null || loginField.getText().length() == 0) {
            errorMessage += "Empty email!\n";
        } else {
            if (!validateEmail(loginField.getText())) {
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

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static boolean validateEmail(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }

    @FXML
    public void onLoginButton(ActionEvent event) throws IOException {
        login();
    }

    @FXML
    private void onRegisterButton(ActionEvent event) throws IOException {
        screensManager.goToRegistrationView();
    }

    public Label getMessageLabel() {
        return messageLabel;
    }
}
