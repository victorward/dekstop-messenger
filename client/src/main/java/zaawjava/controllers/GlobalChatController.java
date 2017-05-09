package zaawjava.controllers;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextArea;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import transport.ChatMessage;
import utils.MessageHandler;
import zaawjava.services.SocketService;
import zaawjava.services.UserService;

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
    private JFXListView<ChatMessage> chatListView;

    @FXML
    private JFXTextArea messageTextArea;

    private ObservableList<ChatMessage> chatMessages = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        chatMessages.add(new ChatMessage(userService.getUser(), "content"));
//        for (int i = 0; i < 200; i++) {
//            chatMessages.add(new ChatMessage(userService.getUser(), "content"));
//
//        }
        chatListView.setItems(chatMessages);

        chatListView.setCellFactory(new Callback<ListView<ChatMessage>, ListCell<ChatMessage>>() {
            @Override
            public ListCell<ChatMessage> call(ListView<ChatMessage> param) {
                return new ListCell<ChatMessage>() {

                    @Override
                    protected void updateItem(ChatMessage item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            StringBuilder sb = new StringBuilder();
                            sb
                                    .append(item.getSender().getFirstName())
                                    .append(" ").append(item.getSender().getLastName())
                                    .append(" : ")
                                    .append(item.getContent());
                            setText(sb.toString());
                        }
                    }
                };
            }
        });

        socketService.on("onGlobalChatMessage", new MessageHandler() {
            @Override
            public void handle(Object msg, Channel channel, ChannelFuture future) {
                log.debug("chat message received: " + ((ChatMessage) msg).getContent());
                Platform.runLater(() -> chatMessages.add((ChatMessage) msg));
            }
        });

    }

    @FXML
    void sendMessageBtn(ActionEvent event) {
        if (messageTextArea.getText().length() == 0) return;
        ChatMessage message = new ChatMessage(userService.getUser(), messageTextArea.getText());
        socketService.emit("newGlobalChatMessage", message);
        messageTextArea.clear();

    }


}
