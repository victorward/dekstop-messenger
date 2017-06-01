package zaawjava.commons.utils;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.group.ChannelGroup;

import java.util.HashMap;

public class MessageService {

    private Channel channel;
    private final HashMap<String, MessageHandler> handlers = new HashMap<>();

    public MessageService(Channel channel) {
        this.channel = channel;
    }

    public MessageService() {
    }

    public void sendMessage(String kind, Object content, MessageHandler handler) throws RuntimeException {
        if (channel == null) {
            throw new RuntimeException("Channel not set");
        }
        handlers.put(kind, handler);
        ChannelFuture future = channel.writeAndFlush(new Message(kind, content));
        handler.setFuture(future);
    }

    public void sendMessage(String kind, Object content) {
        channel.writeAndFlush(new Message(kind, content));

    }

    public void sendMessageToGroup(ChannelGroup group, String kind, Object content) {
        group.writeAndFlush(new Message(kind, content));
    }

    public void sendMessage(String kind, Object content, MessageHandler handler, Channel channel) {
        handlers.put(kind, handler);
        ChannelFuture future = channel.writeAndFlush(new Message(kind, content));
        handler.setFuture(future);
    }

    public void handleMessage(Message msg) {
        if (handlers.containsKey(msg.getKind())) {
            MessageHandler h = handlers.get(msg.getKind());
            h.handle(msg.getContent(), channel, h.getFuture());
        }
    }

    public void registerHandler(String kind, MessageHandler handler) {
        handlers.put(kind, handler);
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}
