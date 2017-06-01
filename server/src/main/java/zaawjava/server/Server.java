package zaawjava.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLException;
import java.security.cert.CertificateException;

@Component
public class Server {
    private static final Logger log = LoggerFactory.getLogger(Server.class);
    private static final int PORT = 8080;
    private static final boolean SSL = true;

    private final ServerConnectionsHandler serverConnectionsHandler;

    @Autowired
    public Server(ServerConnectionsHandler serverConnectionsHandler) {
        this.serverConnectionsHandler = serverConnectionsHandler;
    }

    public void run() {
        SslContext sslCtx;
        if (SSL) {
            try {
                SelfSignedCertificate ssc = new SelfSignedCertificate();

                sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
            } catch (SSLException e) {
                log.error("SSL exception: " + e.getMessage());
                return;
            } catch (CertificateException e) {
                log.error("Certificate exception: " + e.getMessage());
                return;
            }
        } else {
            sslCtx = null;
        }

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            if (sslCtx != null) {
                                p.addLast(sslCtx.newHandler(ch.alloc()));
                            }
                            p.addLast(
                                    new ObjectEncoder(),
                                    new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                    serverConnectionsHandler);
                        }
                    });


            // Bind and start to accept incoming connections.

            ChannelFuture bind = b.bind(PORT);
            log.info("Server running on port: " + PORT);

            bind.sync().channel().closeFuture().sync();

        } catch (InterruptedException e) {
            log.error("Interrupted: " + e.getMessage());
            e.printStackTrace();
        } finally {
            log.info("Shutting down...");
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            log.info("Server is down.");

        }

    }
}
