package zaawjava.server.handlers;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import zaawjava.commons.DTO.UserDTO;
import zaawjava.commons.utils.MessageService;
import zaawjava.server.services.UserService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class UserLoggedOutHandlerTest {
    @Mock
    private UserService userService;
    @Mock
    private MessageService messageService;
    @Mock
    private Channel channel;



    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void handleTest() throws Exception {
        UserDTO userDTO = new UserDTO();
        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);


        UserLoggedOutHandler handler = new UserLoggedOutHandler();
        handler.setMessageService(messageService);
        handler.setUserService(userService);

        handler.handle(userDTO, channel, null);

        verify(userService, times(1)).deleteUserFromLoggedList(userDTO);
        verify(messageService, times(1)).sendMessage("loggedOutUser", "loggedOutUser");

        verify(messageService, times(2)).sendMessageToGroup(any(), argument.capture(), any());

        List<String> arguments = argument.getAllValues();
        assertThat(arguments.contains("numberOfUsersChanged"));
        assertThat(arguments.contains("listOfUsersChanged"));

    }

}