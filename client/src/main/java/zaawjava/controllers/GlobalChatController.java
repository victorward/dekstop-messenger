package zaawjava.controllers;

import DTO.ChatMessageDTO;
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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import utils.MessageHandler;
import zaawjava.services.SocketService;
import zaawjava.services.UserService;
import zaawjava.utils.ChatMessageCellFactory;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class GlobalChatController implements Initializable {
    private static final Logger log = LoggerFactory.getLogger(GlobalChatController.class);


    private UserService userService;
    private final SocketService socketService;

    @Autowired
    public GlobalChatController(SocketService socketService) {
        this.socketService = socketService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @FXML
    private JFXListView<ChatMessageDTO> chatListView;

    @FXML
    private JFXTextArea messageTextArea;

    private ObservableList<ChatMessageDTO> chatMessageDTOS = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        chatListView.setItems(chatMessageDTOS);

        chatListView.setCellFactory(new ChatMessageCellFactory());

        messageTextArea.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    sendChatMessage();
                }
            }
        });

        socketService.on("onGlobalChatMessage", new MessageHandler() {
            @Override
            public void handle(Object msg, Channel channel, ChannelFuture future) {
                log.debug("chat message received: " + ((ChatMessageDTO) msg).getContent());
                Platform.runLater(() -> chatMessageDTOS.add((ChatMessageDTO) msg));
            }
        });

    }

    @FXML
    void sendMessageBtn(ActionEvent event) {
        sendChatMessage();
    }

    private void sendChatMessage() {
        if (messageTextArea.getText().trim().length() == 0) return;
        ChatMessageDTO message = new ChatMessageDTO(userService.getUser(), messageTextArea.getText().trim());
        messageTextArea.setText("");
        socketService.emit("newGlobalChatMessage", message);
    }


}
