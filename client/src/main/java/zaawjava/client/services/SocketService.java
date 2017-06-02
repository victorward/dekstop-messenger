package zaawjava.client.services;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zaawjava.commons.utils.MessageHandler;
import zaawjava.commons.utils.MessageService;
import zaawjava.client.handlers.ClientHandler;

import javax.net.ssl.SSLException;
import java.io.Serializable;
import java.util.concurrent.CompletableFuture;

@Service
public class SocketService {
    private static final Logger log = LoggerFactory.getLogger(SocketService.class);

    static final String HOST = "localhost";
//    static final String HOST = "privatehost.ddns.net";

    static final int PORT = 8080;
    public static final boolean SSL = true;

    private EventLoopGroup group;
    private Channel channel;
    private boolean connected = false;

    private final ClientHandler clientHandler;
    private final MessageService messageService;

    @Autowired
    public SocketService(ClientHandler clientHandler, MessageService messageService) {
        this.clientHandler = clientHandler;
        this.messageService = messageService;
    }

    public boolean isConnected() {
        return connected;
    }

    public ChannelFuture connect() {
        if (connected) throw new RuntimeException("Already connected");

        final SslContext sslCtx;
        if (SSL) {
            try {
                sslCtx = SslContextBuilder.forClient()
                        .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
            } catch (SSLException e) {
                log.error("SSL exception: " + e.getMessage());
                throw new RuntimeException("SSL Exception");
            }
        } else {
            sslCtx = null;
        }

        group = new NioEventLoopGroup();

        Bootstrap b = new Bootstrap();
        b.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();
                        if (sslCtx != null) {
                            p.addLast(sslCtx.newHandler(ch.alloc(), HOST, PORT));
                        }
                        p.addLast(
                                new ObjectEncoder(),
                                new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                clientHandler);
                    }
                });

        ChannelFuture future = b.connect(HOST, PORT).addListener((ChannelFuture cf) -> {
            if (cf.isSuccess()) {
                connected = true;
                channel = cf.channel();
                clientHandler.getMessageService().setChannel(channel);
            } else {
                connected = false;
                cf.channel().close();
                group.shutdownGracefully();
            }
        });

        return future;

    }

    public void disconnect() {
        if (connected) {
            if (channel != null && group != null) {
                channel.close();
                group.shutdownGracefully();
                connected = false;
            }
        }
    }

    public CompletableFuture<Object> emit(String event, Serializable message) {

        CompletableFuture<Object> completableFuture = new CompletableFuture<>();

        messageService.sendMessage(event, message, new MessageHandler() {
            @Override
            public void handle(Object msg, Channel channel, ChannelFuture future) {
                if (future == null) {
                    log.warn("Future is null!");
                    return;
                }
                if (future.isSuccess()) {
                    completableFuture.complete(msg);
                } else {
                    completableFuture.cancel(true);
                }
            }
        });
        return completableFuture;
    }

    public void on(String event, MessageHandler handler) {
        messageService.registerHandler(event, handler);

    }
}
