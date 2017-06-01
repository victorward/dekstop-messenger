package zaawjava.commons.utils;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

public abstract class MessageHandler {
    private ChannelFuture future;

    public abstract void handle(Object msg, Channel channel, ChannelFuture future);

    public ChannelFuture getFuture() {
        return future;
    }

    public void setFuture(ChannelFuture future) {
        this.future = future;
    }
}
