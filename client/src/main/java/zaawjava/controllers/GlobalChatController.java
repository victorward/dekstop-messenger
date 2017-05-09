package zaawjava.controllers;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextArea;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import org.springframework.stereotype.Component;

@Component
public class GlobalChatController {
    @FXML
    private JFXListView<?> chatListView;

    @FXML
    private JFXTextArea messageTextArea;

    @FXML
    void sendMessageBtn(ActionEvent event) {

    }
}
