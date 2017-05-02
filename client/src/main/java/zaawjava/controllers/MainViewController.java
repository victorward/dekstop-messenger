/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zaawjava.controllers;

import DTO.UserDTO;
import io.netty.channel.ChannelFuture;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import utils.MessageHandler;
import zaawjava.ScreensManager;
import zaawjava.services.SocketService;
import zaawjava.services.UserService;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Component
public class MainViewController implements Initializable {

    private ScreensManager screensManager;
    private UserService userService;
    private final SocketService socketService;

    @Autowired
    public MainViewController(SocketService socketService) {
        this.socketService = socketService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setScreensManager(ScreensManager screensManager) {
        this.screensManager = screensManager;
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        socketService.emit("onNumberOfUsers", "").whenComplete((msg, ex) -> {
            if (ex == null) {
                Platform.runLater(() -> loggedUsersLabel.setText(String.valueOf(msg)));
            } else {
                Platform.runLater(() -> loggedUsersLabel.setText("err"));
            }
        });
        socketService.on("numberOfUsersChanged", new MessageHandler() {
            @Override
            public void handle(Object msg, ChannelFuture future) {
                System.out.println("number of users changed! " + msg);

                Platform.runLater(() -> loggedUsersLabel.setText(String.valueOf(msg)));
            }
        });
    }

    @FXML
    private Label loggedUsersLabel;

    @FXML
    void onProfileClick(ActionEvent event) {
        screensManager.goToProfileView();
    }

    @FXML
    void onLogoutClick(ActionEvent event) throws IOException {
        logOutUser(userService.getUser());
        socketService.disconnect();
        screensManager.goToLoginView();
    }

    private void logOutUser(UserDTO user) {
        socketService.emit("loggedOutUser", user).whenComplete((msg, ex) -> {
            if (ex == null) {
                if ("loggedOutUser".equals(msg)) {
                    System.out.println("Logged out");
                } else {
                    Platform.runLater(() -> System.out.println("Logout failed"));
                }
            } else {
                Platform.runLater(() -> System.out.println("Failed during logout actual user"));
            }
        });
    }
}
