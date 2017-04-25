/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zaawjava.controllers;

import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import zaawjava.ScreensManager;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Component
public class MainViewController implements Initializable {

    private Stage stage;
    private Channel channel;
    private EventLoopGroup group;
    private ScreensManager screensManager;

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
        screensManager.goToLoginView();
    }


}
