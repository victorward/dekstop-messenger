package zaawjava.controllers;

import com.jfoenix.controls.JFXTextArea;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import zaawjava.ScreensManager;
import zaawjava.services.SocketService;
import zaawjava.services.UserService;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Yuriy
 */
@Component
public class UserToUserController implements Initializable {
    private ScreensManager screensManager;
    private UserService userService;
    private final SocketService socketService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setScreensManager(ScreensManager screensManager) {
        this.screensManager = screensManager;
    }

    @Autowired
    UserToUserController(SocketService socketService) {
        this.socketService = socketService;
    }

    @FXML
    private JFXTextArea allMessages;

    @FXML
    private Label userName;

    @FXML
    private Label userStatus;

    @FXML
    private JFXTextArea sendMessageArea;

    @FXML
    void sendMessageBtn(ActionEvent event) {

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }
}
