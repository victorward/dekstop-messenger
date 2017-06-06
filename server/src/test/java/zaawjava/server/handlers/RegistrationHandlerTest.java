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

public class RegistrationHandlerTest {
    private RegistrationHandler registrationHandler;
    @Mock
    private MessageService messageService;
    @Mock
    private DatabaseConnector databaseConnector;
    @Mock
    private Channel channel;

    @Before
    public void setUp() {
        registrationHandler = new RegistrationHandler();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void handleTest() throws Exception {
        ArgumentCaptor<String> kindArgument = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> contentArgument = ArgumentCaptor.forClass(String.class);
        UserDTO user = new UserDTO();
        user.setEmail("aaa@gmail.com");

        registrationHandler.setDatabaseConnector(databaseConnector);
        registrationHandler.setMessageService(messageService);

        when(databaseConnector.addNewUser(any())).thenReturn(true);

        registrationHandler.handle(user, channel, null);

        verify(messageService, times(1)).sendMessage(kindArgument.capture(), contentArgument.capture());

        assertThat(kindArgument.getValue()).isEqualTo("onRegistration");
        assertThat(contentArgument.getValue()).isEqualTo("registered");


    }
    @Test
    public void handle2Test() throws Exception {
        ArgumentCaptor<String> kindArgument = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> contentArgument = ArgumentCaptor.forClass(String.class);
        UserDTO user = new UserDTO();
        user.setEmail("aaa@gmail.com");

        registrationHandler.setDatabaseConnector(databaseConnector);
        registrationHandler.setMessageService(messageService);

        when(databaseConnector.addNewUser(any())).thenReturn(false);

        registrationHandler.handle(user, channel, null);

        verify(messageService, times(1)).sendMessage(kindArgument.capture(), contentArgument.capture());

        assertThat(kindArgument.getValue()).isEqualTo("onRegistration");
        assertThat(contentArgument.getValue()).isNotEqualTo("registered");


    }
    @Test
    public void handle3Test() throws Exception {
        ArgumentCaptor<String> kindArgument = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> contentArgument = ArgumentCaptor.forClass(String.class);
        UserDTO user = new UserDTO();
        user.setEmail("aaa@gmail.com");

        registrationHandler.setDatabaseConnector(databaseConnector);
        registrationHandler.setMessageService(messageService);

        when(databaseConnector.addNewUser(any())).thenReturn(true);
        when(databaseConnector.getUserByEmail(any())).thenReturn(user);

        registrationHandler.handle(user, channel, null);

        verify(messageService, times(1)).sendMessage(kindArgument.capture(), contentArgument.capture());

        assertThat(kindArgument.getValue()).isEqualTo("onRegistration");
        assertThat(contentArgument.getValue()).isNotEqualTo("registered");

    }

}