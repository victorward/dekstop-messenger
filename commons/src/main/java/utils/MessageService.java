package utils;

import io.netty.channel.Channel;

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
        channel.writeAndFlush(new Message(kind, content));
    }

    public void sendMessage(String kind, Object content ) {
        try {
            channel.writeAndFlush(new Message(kind, content)).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void sendMessage(String kind, Object content, MessageHandler handler, Channel channel) {
        handlers.put(kind, handler);
        try {
            channel.writeAndFlush(new Message(kind, content)).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void handleMessage(Message msg) {
        if (handlers.containsKey(msg.getKind())) {
            handlers.get(msg.getKind()).handle(msg.getContent());
        }
    }

    public void registerHandler(String kind, MessageHandler handler){
        handlers.put(kind, handler);
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}
