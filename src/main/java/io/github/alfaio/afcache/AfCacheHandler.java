package io.github.alfaio.afcache;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author LinMF
 * @since 2024/6/12
 **/
public class AfCacheHandler extends SimpleChannelInboundHandler<String> {

    public static final String CRLF = "\r\n";
    public static final String STR_PREFIX = "+";
    public static final String OK = "OK";
    public static final String INFO = "AfCache server[1.0.0], created by Alfa." + CRLF
            + "Mock redis server at 2024-06-12 in XiaMen." + CRLF;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String message) throws Exception {
        String[] args = message.split(CRLF);
        System.out.println("AfCacheHandler ===>" + String.join(",", args));
        String cmd = args[2].toUpperCase();
        if ("COMMAND".equals(cmd)) {
            writeByteBuffer(ctx, "*2"
                    + CRLF + "*7"
                    + CRLF + "COMMAND"
                    + CRLF + "$4"
                    + CRLF + "PING");
        } else if ("PING".equals(cmd)) {
            String result = "PONG";
            if (args.length >= 5) {
                result = args[4];
            }
            writeByteBuffer(ctx, STR_PREFIX + result + CRLF);
        } else if ("INFO".equals(cmd)) {
            bulkString(ctx, INFO);
        } else {
            simpleString(ctx, OK);
        }
    }

    private void bulkString(ChannelHandlerContext ctx, String content) {
        writeByteBuffer(ctx, "$" + content.getBytes().length + CRLF + content + CRLF);
    }

    private void simpleString(ChannelHandlerContext ctx, String content) {
        writeByteBuffer(ctx, STR_PREFIX + content + CRLF);
    }

    private void writeByteBuffer(ChannelHandlerContext ctx, String content) {
        System.out.println("wrap byte buffer and reply ===>" + content);
        ByteBuf buffer = Unpooled.buffer(128);
        buffer.writeBytes(content.getBytes());
        ctx.writeAndFlush(buffer);
    }
}
