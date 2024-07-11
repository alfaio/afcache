package io.github.alfaio.afcache.core;

import io.github.alfaio.afcache.core.command.Commands;
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
    public static final String BULK_PREFIX = "$";
    public static final String OK = "OK";
    public static final AfCache CACHE = new AfCache();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String message) throws Exception {
        String[] args = message.split(CRLF);
        System.out.println("AfCacheHandler ===>" + String.join(",", args));
        String cmd = args[2].toUpperCase();

        Command command = Commands.get(cmd);
        if (command != null) {
            try {
                Reply<?> reply = command.exec(CACHE, args);
                System.out.println("CMD[" + cmd + "} => " + reply.type + " => " + reply.value);
                replyContext(ctx, reply);
            } catch (Exception e) {
                Reply<String> reply = Reply.error("EXP exception with msg: '" + e.getMessage() + "'");
                replyContext(ctx, reply);
            }

        } else {
            Reply<String> reply = Reply.error("ERR unsupported command '" + cmd + "'");
            replyContext(ctx, reply);
        }
    }

    private void replyContext(ChannelHandlerContext ctx, Reply<?> reply) {
        switch (reply.type) {
            case INT -> integer(ctx, (Integer) reply.value);
            case SIMPLE_STRING -> simpleString(ctx, (String) reply.value);
            case BULK_STRING -> bulkString(ctx, (String) reply.value);
            case ARRAY -> array(ctx, (String[]) reply.value);
            case ERROR -> error(ctx, (String) reply.value);
            default -> simpleString(ctx, OK);
        }
    }

    private void error(ChannelHandlerContext ctx, String message) {
        writeByteBuffer(ctx, errorEncode(message));
    }

    private void array(ChannelHandlerContext ctx, String[] array) {
        writeByteBuffer(ctx, arrayEncode(array));
    }

    private void integer(ChannelHandlerContext ctx, int i) {
        writeByteBuffer(ctx, ":" + i + CRLF);
    }

    private void bulkString(ChannelHandlerContext ctx, String content) {
        writeByteBuffer(ctx, bulkStringEncode(content));
    }

    private void simpleString(ChannelHandlerContext ctx, String content) {
        writeByteBuffer(ctx, simpleStringEncode(content));
    }

    private static String errorEncode(String msg) {
        return "-" + msg + CRLF;
    }

    private static String integerEncode(int i) {
        return ":" + i + CRLF;
    }

    private static String arrayEncode(Object[] array) {
        StringBuilder sb = new StringBuilder();
        if (array == null) {
            sb.append("*-1" + CRLF);
        } else if (array.length == 0) {
            sb.append("*0" + CRLF);
        } else {
            sb.append("*").append(array.length).append(CRLF);
            for (Object obj : array) {
                if (obj == null) {
                    sb.append("$-1" + CRLF);
                } else if (obj instanceof Integer) {
                    sb.append(integerEncode((Integer) obj));
                } else if (obj instanceof String) {
                    sb.append(bulkStringEncode((String) obj));
                } else if (obj instanceof Object[]) {
                    sb.append(arrayEncode((Object[]) obj));
                }
            }
        }
        return sb.toString();
    }

    private static String bulkStringEncode(String content) {
        String result;
        if (content == null) {
            result = "$-1";
        } else if (content.isEmpty()) {
            result = "$0";
        } else {
            result = BULK_PREFIX + content.getBytes().length + CRLF + content;
        }
        return result + CRLF;
    }

    private static String simpleStringEncode(String content) {
        String result;
        if (content == null) {
            result = "$-1";
        } else if (content.isEmpty()) {
            result = "$0";
        } else {
            result = STR_PREFIX + content;
        }
        return result + CRLF;
    }

    private void writeByteBuffer(ChannelHandlerContext ctx, String content) {
        System.out.println("wrap byte buffer and reply ===>" + content);
        ByteBuf buffer = Unpooled.buffer(128);
        buffer.writeBytes(content.getBytes());
        ctx.writeAndFlush(buffer);
    }
}
