package zaawjava.utils;


import DTO.ChatMessageDTO;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class ChatMessageCellFactory implements Callback<ListView<ChatMessageDTO>, ListCell<ChatMessageDTO>> {

    @Override
    public ListCell<ChatMessageDTO> call(ListView<ChatMessageDTO> param) {
        return new ListCell<ChatMessageDTO>() {

            @Override
            protected void updateItem(ChatMessageDTO item, boolean empty) {
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
}
