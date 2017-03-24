package utils;

import io.netty.channel.ChannelFuture;

public abstract class MessageHandler {
    private ChannelFuture future;
    public abstract void handle(Object msg, ChannelFuture future);

    public ChannelFuture getFuture() {
        return future;
    }

    public void setFuture(ChannelFuture future) {
        this.future = future;
    }
}
