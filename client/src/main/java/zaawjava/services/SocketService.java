package zaawjava.services;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.util.concurrent.Promise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zaawjava.handlers.ClientHandler;

@Service
public class SocketService {
    static final String HOST = "localhost";
    static final int PORT = 8080;

    private EventLoopGroup group;
    private Channel channel;

    private final ClientHandler clientHandler;

    @Autowired
    public SocketService(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }


    public ChannelFuture connect() {
        group = new NioEventLoopGroup();

        Promise promise = null;

        Bootstrap b = new Bootstrap();
        b.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();

                        p.addLast(
                                new ObjectEncoder(),
                                new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                clientHandler);
                    }
                });

        ChannelFuture future = b.connect(HOST, PORT).addListener((ChannelFuture cf) -> {
            if (cf.isSuccess()) {
                channel = cf.channel();
                clientHandler.getMessageService().setChannel(channel);
            } else {
                cf.channel().close();
                group.shutdownGracefully();
            }
        });

        return future;


    }
}
