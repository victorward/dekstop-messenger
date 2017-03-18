package zaawjava.controllers;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zaawjava.handlers.ClientHandler;


public class LoginController {
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    static final String HOST = "localhost";
    static final int PORT = 2222;

    private Channel channel;
    private EventLoopGroup group;


    @FXML
    private Label messageLabel;
    @FXML
    private TextField loginField;
    @FXML
    private TextField passwordField;

    public LoginController() {
        connect();

    }

    public void connect() {
        group = new NioEventLoopGroup();

        Task<Channel> task = new Task<Channel>() {
            @Override
            protected Channel call() throws Exception {
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
                                        new ClientHandler());
                            }
                        });


                return b.connect(HOST, PORT).sync().channel();
            }

            @Override
            protected void succeeded() {
                log.debug("connected");
                channel = getValue();
            }

            @Override
            protected void failed() {
                log.debug("connection failed");
            }
        };

        new Thread(task).start();
    }

    public void disconnect() {
        Task<Void> task = new Task<Void>() {

            @Override
            protected Void call() throws Exception {
                channel.close().sync();
                group.shutdownGracefully().sync();

                return null;
            }

            @Override
            protected void succeeded() {


            }

            @Override
            protected void failed() {

            }

        };

        new Thread(task).start();

    }

    @FXML
    public void onLoginButton() {
        channel.write(new User(loginField.getText(), passwordField.getText()));
        channel.flush();


    }

}
