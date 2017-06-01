/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zaawjava.client.controllers;

import zaawjava.commons.DTO.UserDTO;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import zaawjava.commons.utils.MessageHandler;
import zaawjava.client.ScreensManager;
import zaawjava.client.services.SocketService;
import zaawjava.client.services.UserService;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
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
    private TableView<Map.Entry<UserDTO, Boolean>> usersList;
    @FXML
    private TableColumn<Map.Entry<UserDTO, Boolean>, String> userName;
    @FXML
    private TableColumn<Map.Entry<UserDTO, Boolean>, String> userStatus;
    @FXML
    private Label loggedUsersLabel;
    @FXML
    private Label actualUser;

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
        screensManager.getStage().setOnCloseRequest(event1 -> {
            logOutUser(userService.getUser());
            socketService.disconnect();
        });
        Platform.runLater(() -> actualUser.setText(userService.getUser().getFirstName() + " " + userService.getUser().getLastName()));
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
        socketService.on("listOfUsersChanged", new MessageHandler() {
            @Override
            public void handle(Object msg, Channel channel, ChannelFuture future) {
                Platform.runLater(() -> {
                    HashMap<UserDTO, Boolean> list = (HashMap<UserDTO, Boolean>) msg;
                    ObservableList<Map.Entry<UserDTO, Boolean>> items = FXCollections.observableArrayList(list.entrySet());
                    usersList.setItems(items);
                });
            }
        });

        usersList.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                screensManager.setPrivateMessageController(usersList.getSelectionModel().getSelectedItem().getKey());
            }
        });
        initHashTable();
    }

    private void initHashTable() {
        userName.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<UserDTO, Boolean>, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Map.Entry<UserDTO, Boolean>, String> p) {
                return new SimpleStringProperty(p.getValue().getKey().getFirstName() + " " + p.getValue().getKey().getLastName());
            }
        });

        userStatus.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<UserDTO, Boolean>, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Map.Entry<UserDTO, Boolean>, String> p) {
                if (p.getValue().getValue()) {
                    return new SimpleStringProperty("Online");
                } else {
                    return new SimpleStringProperty("");
                }
            }
        });
        usersList.getColumns().setAll(userName, userStatus);

        socketService.emit("getUsersStatus", "").whenComplete((msg, ex) -> {
            if (ex == null) {
                Platform.runLater(() -> {
                    HashMap<UserDTO, Boolean> list = (HashMap<UserDTO, Boolean>) msg;
                    ObservableList<Map.Entry<UserDTO, Boolean>> items = FXCollections.observableArrayList(list.entrySet());
                    usersList.setItems(items);
                });
            } else {
                Platform.runLater(() -> loggedUsersLabel.setText("status error"));
            }
        });
    }

    @FXML
    void onProfileClick(ActionEvent event) {
        screensManager.setProfileView();
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

    @FXML
    private void goToWAMY() {
        screensManager.setGlobalChatView();
    }

    @FXML
    private void goToMeInList() {
        Platform.runLater(() -> {
            UserDTO userDTO = userService.getUser();
            for (Map.Entry<UserDTO, Boolean> user : usersList.getItems()) {
                if (user.getKey().getId() == userDTO.getId()) {
                    usersList.getSelectionModel().select(user);
                }
            }
        });
    }

    public Pane getContentPane() {
        return contentPane;
    }

    public void setContentPane(Pane contentPane) {
        this.contentPane = contentPane;
    }
}
