package com.whf.util;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author whfstudio@163.com
 * @date 2017/11/20
 */
public class NettyClientHandler extends SimpleChannelInboundHandler<BaseMsg> {

    private static final Logger logger = LoggerFactory.getLogger(NettyClientHandler.class);

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            switch (e.state()) {
                case WRITER_IDLE:
                    PingMsg pingMsg = new PingMsg();
                    ctx.writeAndFlush(pingMsg);
                    System.out.println("send ping to server----------");
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, BaseMsg baseMsg) throws Exception {
        MsgType msgType = baseMsg.getType();
        switch (msgType) {
            case LOGIN: {
                //向服务器发起登录
                LoginMsg loginMsg = new LoginMsg();
                loginMsg.setPassword("123");
                loginMsg.setUserName("wuhf");
                channelHandlerContext.writeAndFlush(loginMsg);
            }
            break;
            case PING: {
                logger.info("receive ping from server----------");
            }
            break;
            case ASK: {
                ReplyClientBody replyClientBody = new ReplyClientBody("util info **** !!!");
                ReplyMsg replyMsg = new ReplyMsg();
                replyMsg.setBody(replyClientBody);
                channelHandlerContext.writeAndFlush(replyMsg);
            }
            break;
            case REPLY: {
                ReplyMsg replyMsg = (ReplyMsg) baseMsg;
                ReplyServerBody replyServerBody = (ReplyServerBody) replyMsg.getBody();
               logger.info("receive util msg: " + replyServerBody.getServerInfo());
            }
            default:
                break;
        }
        ReferenceCountUtil.release(msgType);
    }
}
