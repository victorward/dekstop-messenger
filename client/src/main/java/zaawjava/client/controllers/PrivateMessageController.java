package zaawjava.client.controllers;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextArea;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.controlsfx.control.Notifications;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import zaawjava.client.ScreensManager;
import zaawjava.client.services.SocketService;
import zaawjava.client.services.UserService;
import zaawjava.client.utils.ChatMessageCellFactory;
import zaawjava.commons.DTO.ChatMessageDTO;
import zaawjava.commons.DTO.UserDTO;
import zaawjava.commons.utils.MessageHandler;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

/**
 * Created by Yuriy
 */
@Component
public class PrivateMessageController implements Initializable {
    private static final Logger log = LoggerFactory.getLogger(PrivateMessageController.class);

    private ScreensManager screensManager;
    private UserService userService;
    private final SocketService socketService;

    private UserDTO userDTO;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setScreensManager(ScreensManager screensManager) {
        this.screensManager = screensManager;
    }

    @Autowired
    PrivateMessageController(SocketService socketService) {
        this.socketService = socketService;
    }

    @FXML
    private JFXListView<ChatMessageDTO> messagesListView;

    @FXML
    private Label userName;

    @FXML
    private JFXTextArea sendMessageArea;

    @FXML
    private ImageView userAvatar;

    private ObservableList<ChatMessageDTO> chatMessageDTOS = FXCollections.observableArrayList();
    private CompletableFuture<Image> prevImageFuture;

    @FXML
    void sendMessageBtn(ActionEvent event) {
        sendChatMessage();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        chatMessageDTOS.clear();
        messagesListView.setItems(chatMessageDTOS);
        messagesListView.setCellFactory(new ChatMessageCellFactory());
        sendMessageArea.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    sendChatMessage();
                }
            }
        });
    }

    public void setUserDTO(UserDTO userDTO) {
        this.userDTO = userDTO;
        socketService.emit("getConversation", userDTO).whenComplete((msg, ex) -> {
            Platform.runLater(() -> chatMessageDTOS.addAll((List<ChatMessageDTO>) msg));
            socketService.on("privateMessage", new MessageHandler() {
                @Override
                public void handle(Object msg, Channel channel, ChannelFuture future) {
                    Platform.runLater(() -> {
                        ClassLoader classLoader = getClass().getClassLoader();
                        ChatMessageDTO newChatMessage = (ChatMessageDTO) msg;
                        if (newChatMessage.getSender().getId() != userDTO.getId()) {
                            if (newChatMessage.getRecipient().getId() == userService.getUser().getId()) {
                                Media hit = null;
                                try {
                                    hit = new Media(classLoader.getResource("sounds/tuturu.mp3").toURI().toString());
                                    MediaPlayer mediaPlayer = new MediaPlayer(hit);
                                    mediaPlayer.play();
                                } catch (URISyntaxException e) {
                                    log.warn("cannot load resource:", e);
                                }

                                Notifications.create().position(Pos.BOTTOM_RIGHT)
                                        .text(newChatMessage.getSender().getFirstName() + " "
                                                + newChatMessage.getSender().getLastName() + ": \n"
                                                + newChatMessage.getContent())
                                        .show();
                            }
                        }
                        if (newChatMessage.getSender().getId() == userDTO.getId()
                                || newChatMessage.getSender().getId() == userService.getUser().getId()) {
                            chatMessageDTOS.add(newChatMessage);
                        }
                    });
                }
            });
        });

        if (userDTO != null) {
            userName.setText("");
            userName.setText(userDTO.getFirstName() + " " + userDTO.getLastName());
            Platform.runLater(() -> setProfileAvatar(userDTO));
        }

    }

    void setProfileAvatar(UserDTO userDTO) {
        String imageSource = userDTO.getPhoto();
        if (imageSource != null && !imageSource.equals("")) {
            if (prevImageFuture != null && !prevImageFuture.isDone()) {
                prevImageFuture.cancel(false);
            }
            prevImageFuture = CompletableFuture.supplyAsync(() -> new Image(imageSource)).whenComplete((img, ex) -> {
                Platform.runLater(() -> userAvatar.setImage(img));
            });
        }
    }

    private void sendChatMessage() {
        if (sendMessageArea.getText().trim().length() == 0)
            return;
        ChatMessageDTO message = new ChatMessageDTO(userService.getUser(), sendMessageArea.getText().trim());
        message.setRecipient(userDTO);
        message.setDate(new Date());
        sendMessageArea.setText("");
        socketService.emit("newPrivateMessage", message);
    }
}
