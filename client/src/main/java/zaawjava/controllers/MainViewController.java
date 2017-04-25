/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zaawjava.controllers;

import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import zaawjava.ScreensManager;
import zaawjava.services.SocketService;
import zaawjava.services.UserService;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Component
public class MainViewController implements Initializable {

    private Stage stage;
    private Channel channel;
    private EventLoopGroup group;
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
        // TODO
    }


    @FXML
    void onProfileClick(ActionEvent event) {
        screensManager.goToProfileView();
    }

    @FXML
    void onLogoutClick(ActionEvent event) throws IOException {
        logOutUser(userService.getUser());
        screensManager.goToLoginView();
    }

    private void logOutUser(User user) {
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
