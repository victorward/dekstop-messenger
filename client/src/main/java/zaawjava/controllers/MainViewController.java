/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zaawjava.controllers;

import DTO.UserDTO;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import javafx.util.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import utils.MessageHandler;
import zaawjava.ScreensManager;
import zaawjava.services.SocketService;
import zaawjava.services.UserService;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

@Component
public class MainViewController implements Initializable {

    private ScreensManager screensManager;
    private UserService userService;
    private final SocketService socketService;

    @FXML
    private Pane contentPane;
    @FXML
    private TableView<UserDTO> usersList;
    @FXML
    private TableColumn<UserDTO, String> userName;
    @FXML
    private TableColumn<UserDTO, String> userStatus;
    @FXML
    private Label loggedUsersLabel;

    ObservableMap<UserDTO, Boolean> listOfUsersStatus;
    ObservableList<UserDTO> listOfUsers;
    Timeline fiveSecondsWonder;


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
            public void handle(Object msg, Channel channel, ChannelFuture future) {
                System.out.println("number of users changed! " + msg);

                Platform.runLater(() -> loggedUsersLabel.setText(String.valueOf(msg)));
            }
        });
        listOfUsers = FXCollections.observableArrayList();
        listOfUsersStatus = FXCollections.observableHashMap();
        initUserList();
        initTimerForUsersUpdate();
    }

    private void initTimerForUsersUpdate() {
        Platform.runLater(() -> {
            fiveSecondsWonder = new Timeline(new KeyFrame(Duration.seconds(5), new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    initUserList();
                }
            }));
            fiveSecondsWonder.setCycleCount(Timeline.INDEFINITE);
            fiveSecondsWonder.play();
        });
    }

    private void initUserList() {
        userName.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<UserDTO, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<UserDTO, String> p) {
                return new SimpleStringProperty(p.getValue().getFirstName() + " " + p.getValue().getLastName());
            }
        });
        userStatus.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<UserDTO, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<UserDTO, String> p) {
                return new SimpleStringProperty(checkUserStatusOnList(p.getValue()));
            }
        });

        socketService.emit("checkUserList", "").whenComplete((msg, ex) -> {
            if (ex == null) {
                Platform.runLater(() -> {
                    listOfUsers.clear();
                    listOfUsers.addAll((List<UserDTO>) msg);
                    usersList.setItems(listOfUsers);
                });
            } else {
                Platform.runLater(() -> loggedUsersLabel.setText("err"));
            }
        });
        socketService.emit("getUsersStatus", "").whenComplete((msg, ex) -> {
            if (ex == null) {
                Platform.runLater(() -> {
                    listOfUsersStatus.clear();
                    listOfUsersStatus.putAll((HashMap<UserDTO, Boolean>) msg);
                });
            } else {
                Platform.runLater(() -> loggedUsersLabel.setText("status error"));
            }
        });
    }

    private String checkUserStatusOnList(UserDTO userDTO) {
        for (Map.Entry<UserDTO, Boolean> entry : listOfUsersStatus.entrySet()) {
            if (entry.getKey().getId() == userDTO.getId() && entry.getValue()) {
                return "Online";
            }
        }
        return "";
    }

    @FXML
    void onProfileClick(ActionEvent event) {
        screensManager.goToProfileView();
    }

    @FXML
    void onLogoutClick(ActionEvent event) throws IOException {
        logOutUser(userService.getUser());
        fiveSecondsWonder.stop();
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

    @FXML
    private void goToWAMY() {
        screensManager.goToMainView();
    }

    public Pane getContentPane() {
        return contentPane;
    }

    public void setContentPane(Pane contentPane) {
        this.contentPane = contentPane;
    }
}
