package utils;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class MessageService {

    private Channel channel;
    private HashMap<String, MessageHandler> handlers = new HashMap<>();

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
        channel.writeAndFlush(new Message(kind, content)).addListener((GenericFutureListener<ChannelFuture>) handler::setFuture);
    }

    public void sendMessage(String kind, Object content) {
        try {
            channel.writeAndFlush(new Message(kind, content)).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String kind, Object content, MessageHandler handler, Channel channel) {
        handlers.put(kind, handler);
        handlers.put(kind, handler);
        channel.writeAndFlush(new Message(kind, content)).addListener((GenericFutureListener<ChannelFuture>) handler::setFuture);

    }

    public void handleMessage(Message msg) {
        if (handlers.containsKey(msg.getKind())) {
            MessageHandler h =  handlers.get(msg.getKind());
            h.handle(msg.getContent(), h.getFuture());
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
