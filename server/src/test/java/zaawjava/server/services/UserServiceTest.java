package zaawjava.server.services;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelId;
import io.netty.util.concurrent.GenericFutureListener;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import zaawjava.commons.DTO.UserDTO;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserServiceTest {

    private UserService userService;

    @Mock
    Channel channel;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        when(channel.id()).thenReturn(mock(ChannelId.class));
        userService = new UserService();
    }

    @Test
    public void addUserToLoggedList() throws Exception {
        UserDTO user = new UserDTO();

        ChannelFuture cf = mock(ChannelFuture.class);

        when(channel.closeFuture()).thenReturn(cf);

        userService.addUserToLoggedList(user, channel);

        assertThat(userService.getNumberOfLoggedUsers()).isEqualTo(1);

    }

    @Test
    public void deleteUserFromLoggedListByUser() throws Exception {

        UserDTO user = new UserDTO();
        user.setId(123);

        ChannelFuture cf = mock(ChannelFuture.class);

        when(channel.closeFuture()).thenReturn(cf);
        userService.addUserToLoggedList(user, channel);

        userService.deleteUserFromLoggedList(user);

        assertThat(userService.getNumberOfLoggedUsers()).isEqualTo(0);

    }

    @Test
    public void deleteUserFromLoggedListByChannelClose() throws Exception {

        UserDTO user = new UserDTO();
        user.setId(123);

        ChannelFuture cf = mock(ChannelFuture.class);
        ArgumentCaptor<GenericFutureListener> captor = ArgumentCaptor.forClass(GenericFutureListener.class);
        when(cf.addListener(captor.capture())).thenReturn(cf);
        when(channel.closeFuture()).thenReturn(cf);

        userService.addUserToLoggedList(user, channel);
        assertThat(userService.getNumberOfLoggedUsers()).isEqualTo(1);

        captor.getValue().operationComplete(cf);

        assertThat(userService.getNumberOfLoggedUsers()).isEqualTo(0);

    }

    @Test
    public void deleteUserFromLoggedListByChannel() throws Exception {
        UserDTO user = new UserDTO();
        ChannelFuture cf = mock(ChannelFuture.class);

        when(channel.closeFuture()).thenReturn(cf);
        userService.addUserToLoggedList(user, channel);

        userService.deleteUserFromLoggedList(channel);

        assertThat(userService.getNumberOfLoggedUsers()).isEqualTo(0);

    }

    @Test
    public void checkIfLogged1() throws Exception {
        UserDTO user = new UserDTO();
        user.setId(123);
        ChannelFuture cf = mock(ChannelFuture.class);

        when(channel.closeFuture()).thenReturn(cf);
        userService.addUserToLoggedList(user, channel);

        assertThat(userService.checkIfLogged(user)).isTrue();

    }

    @Test
    public void checkIfLogged2() throws Exception {
        UserDTO user = new UserDTO();
        user.setId(123);
        assertThat(userService.checkIfLogged(user)).isFalse();
    }

    @Test
    public void getListOfLoggedUsers() throws Exception {
        UserDTO user1 = new UserDTO();
        user1.setId(1);
        UserDTO user2 = new UserDTO();
        user2.setId(2);

        ChannelFuture cf = mock(ChannelFuture.class);

        Channel channel1 = mock(Channel.class);
        when(channel1.id()).thenReturn(mock(ChannelId.class));

        Channel channel2 = mock(Channel.class);
        when(channel2.id()).thenReturn(mock(ChannelId.class));


        when(channel1.closeFuture()).thenReturn(cf);
        when(channel2.closeFuture()).thenReturn(cf);

        userService.addUserToLoggedList(user1, channel1);
        userService.addUserToLoggedList(user2, channel2);

        List<UserDTO> listOfLoggedUsers = userService.getListOfLoggedUsers();

        assertThat(listOfLoggedUsers.size()).isEqualTo(2);

        assertThat(listOfLoggedUsers.contains(user1));
        assertThat(listOfLoggedUsers.contains(user2));

    }

    @Test
    public void getUserChannel() throws Exception {
        UserDTO user1 = new UserDTO();
        user1.setId(1);
        UserDTO user2 = new UserDTO();
        user2.setId(2);

        ChannelFuture cf = mock(ChannelFuture.class);

        Channel channel1 = mock(Channel.class);
        when(channel1.id()).thenReturn(mock(ChannelId.class));

        Channel channel2 = mock(Channel.class);
        when(channel2.id()).thenReturn(mock(ChannelId.class));


        when(channel1.closeFuture()).thenReturn(cf);
        when(channel2.closeFuture()).thenReturn(cf);

        userService.addUserToLoggedList(user1, channel1);
        userService.addUserToLoggedList(user2, channel2);

        assertThat(userService.getUserChannel(user1)).isEqualTo(channel1);
        assertThat(userService.getUserChannel(user2)).isEqualTo(channel2);
    }

    @Test
    public void getUserByChannel() throws Exception {

        UserDTO user1 = new UserDTO();
        user1.setId(1);
        UserDTO user2 = new UserDTO();
        user2.setId(2);

        ChannelFuture cf = mock(ChannelFuture.class);

        Channel channel1 = mock(Channel.class);
        when(channel1.id()).thenReturn(mock(ChannelId.class));

        Channel channel2 = mock(Channel.class);
        when(channel2.id()).thenReturn(mock(ChannelId.class));

        when(channel1.closeFuture()).thenReturn(cf);
        when(channel2.closeFuture()).thenReturn(cf);

        userService.addUserToLoggedList(user1, channel1);
        userService.addUserToLoggedList(user2, channel2);

        assertThat(userService.getUserByChannel(channel1)).isEqualTo(user1);
        assertThat(userService.getUserByChannel(channel2)).isEqualTo(user2);

    }


}