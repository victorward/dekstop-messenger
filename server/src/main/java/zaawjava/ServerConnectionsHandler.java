package zaawjava;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.Message;
import utils.MessageService;

public class ServerConnectionsHandler extends ChannelInboundHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(ServerConnectionsHandler.class);
    private MessageService messageService;

    public ServerConnectionsHandler() {
        messageService = new MessageService();
        messageService.registerHandler("onLogin", (msg) -> {
            log.debug("login!" + msg);
            messageService.sendMessage("onLogin", "loggedIn");
        });
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug("Channel Active");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            log.debug("message recived: " + msg);
            messageService.setChannel(ctx.channel());
            messageService.handleMessage((Message) msg);

        } catch (ClassCastException e) {
            log.warn("Not message recived");
        }

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        log.debug("read complete");
        ctx.flush();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //disconnected
        log.debug("channel inactive");

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
