package zaawjava.server.handlers;

import io.netty.channel.Channel;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import zaawjava.commons.DTO.UserDTO;
import zaawjava.commons.utils.MessageService;
import zaawjava.server.services.DatabaseConnector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


public class UpdateUserHandlerTest {

    private UpdateUserHandler updateUserHandler;
    @Mock
    private MessageService messageService;
    @Mock
    private DatabaseConnector databaseConnector;
    @Mock
    private Channel channel;

    @Before
    public void setUp() {
        updateUserHandler = new UpdateUserHandler();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void handleTest() throws Exception {
        ArgumentCaptor<String> kindArgument = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> contentArgument = ArgumentCaptor.forClass(String.class);

        UserDTO user = new UserDTO();


        updateUserHandler.setDatabaseConnector(databaseConnector);
        updateUserHandler.setMessageService(messageService);

        updateUserHandler.handle(user, channel, null);

        verify(messageService, times(1)).sendMessage(kindArgument.capture(), contentArgument.capture());

        assertThat(kindArgument.getValue()).isEqualTo("updateUser");
        assertThat(contentArgument.getValue()).isEqualTo("updated");

    }

    @Test
    public void handle2Test() throws Exception {
        ArgumentCaptor<String> kindArgument = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> contentArgument = ArgumentCaptor.forClass(String.class);

        UserDTO user = new UserDTO();

        updateUserHandler.setDatabaseConnector(databaseConnector);
        updateUserHandler.setMessageService(messageService);

        doThrow(new RuntimeException()).when(databaseConnector).updateUser(any());

        updateUserHandler.handle(user, channel, null);

        verify(messageService, times(1)).sendMessage(kindArgument.capture(), contentArgument.capture());

        assertThat(kindArgument.getValue()).isEqualTo("updateUser");
        assertThat(contentArgument.getValue()).isNotEqualTo("updated");

    }

}