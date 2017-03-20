/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zaawjava.controllers;

import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import javafx.fxml.Initializable;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class MainViewController implements Initializable {

    private Stage stage;
    private Channel channel;
    private EventLoopGroup group;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }


    public void setParameters(Stage stage, Channel channel, EventLoopGroup group) {
        this.stage = stage;
        this.channel = channel;
        this.group = group;

    }
}
