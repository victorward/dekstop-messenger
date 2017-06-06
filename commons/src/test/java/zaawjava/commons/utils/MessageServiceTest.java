package zaawjava.commons.utils;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;


public class MessageServiceTest {
    private MessageService messageService;

    @Mock
    private Channel channel;

    @Before
    public void setUp() {
        messageService = new MessageService();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void handleMessageTest() throws Exception {
        Message message = new Message("customEvent", "content");

        MessageHandler handler = spy(new MessageHandler() {
            @Override
            public void handle(Object msg, Channel ch, ChannelFuture future) {
                assertThat((String) msg).isEqualTo("content");
                assertThat(ch).isEqualTo(channel);
                assertThat(future).isNull();
            }
        });

        messageService.registerHandler("customEvent", handler);

        messageService.setChannel(channel);
        messageService.handleMessage(message);

        verify(handler, times(1)).handle(any(), any(), any());
    }

    @Test
    public void handleMessageTest2() throws Exception {
        Message message = new Message("wrongMessageName", "content");

        MessageHandler handler = spy(new MessageHandler() {
            @Override
            public void handle(Object msg, Channel ch, ChannelFuture future) {
                fail();
            }
        });

        messageService.registerHandler("customEvent", handler);

        messageService.setChannel(channel);
        messageService.handleMessage(message);

        verify(handler, times(0)).handle(any(), any(), any());
    }

    @Test
    public void sendMessageExceptionTest() {
        MessageHandler handler = spy(new MessageHandler() {
            @Override
            public void handle(Object msg, Channel ch, ChannelFuture future) {
                fail();
            }
        });
        assertThatThrownBy(() -> messageService.sendMessage("kind", "content", handler)).isInstanceOf(RuntimeException.class);
    }

    @Test
    public void sendMessageWithResponseTest() {
        Message responseMessage = new Message("eventName", "responseContent");

        ChannelFuture cf = mock(ChannelFuture.class);

        when(channel.writeAndFlush(any())).thenReturn(cf);

        MessageHandler handler = spy(new MessageHandler() {
            @Override
            public void handle(Object msg, Channel ch, ChannelFuture future) {
                assertThat((String) msg).isEqualTo("responseContent");
                assertThat(ch).isEqualTo(channel);
                assertThat(future).isEqualTo(cf);
            }
        });

        messageService.setChannel(channel);
        messageService.sendMessage("eventName", "content to send", handler);

        messageService.handleMessage(responseMessage);
        verify(handler, times(1)).handle(any(), any(), any());

    }

    @Test
    public void registerHandlerWithExistingEventName() throws Exception {
        Message message = new Message("eventName", "content");

        MessageHandler handler1 = spy(new MessageHandler() {
            @Override
            public void handle(Object msg, Channel ch, ChannelFuture future) {
                fail();
            }
        });

        MessageHandler handler2 = spy(new MessageHandler() {
            @Override
            public void handle(Object msg, Channel ch, ChannelFuture future) {
                assertThat((String) msg).isEqualTo("content");
                assertThat(ch).isEqualTo(channel);
                assertThat(future).isNull();
            }
        });

        messageService.registerHandler("eventName", handler1);
        messageService.registerHandler("eventName", handler2);

        messageService.setChannel(channel);
        messageService.handleMessage(message);

        verify(handler1, times(0)).handle(any(), any(), any());
        verify(handler2, times(1)).handle(any(), any(), any());

    }


}