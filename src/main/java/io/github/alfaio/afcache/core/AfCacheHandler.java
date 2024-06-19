package io.github.alfaio.afcache.core;

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
    public static final String INFO = "AfCache server[1.0.0], created by Alfa." + CRLF
            + "Mock redis server at 2024-06-12 in XiaMen." + CRLF;
    public static final AfCache CACHE = new AfCache();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String message) throws Exception {
        String[] args = message.split(CRLF);
        System.out.println("AfCacheHandler ===>" + String.join(",", args));
        String cmd = args[2].toUpperCase();
        if ("COMMAND".equals(cmd)) {
            writeByteBuffer(ctx, "*2"
                    + CRLF + "$7"
                    + CRLF + "COMMAND"
                    + CRLF + "$4"
                    + CRLF + "PING"
                    + CRLF);
        } else if ("PING".equals(cmd)) {
            String result = "PONG";
            if (args.length >= 5) {
                result = args[4];
            }
            simpleString(ctx, result);
        } else if ("INFO".equals(cmd)) {
            bulkString(ctx, INFO);
        } else if ("SET".equals(cmd)) {
            CACHE.set(args[4], args[6]);
            simpleString(ctx, OK);
        } else if ("GET".equals(cmd)) {
            String value = CACHE.get(args[4]);
            bulkString(ctx, value);
        } else if ("STRLEN".equals(cmd)) {
            String value = CACHE.get(args[4]);
            bulkString(ctx, value);
            integer(ctx, value == null ? 0 : value.length());
        } else if ("DEL".equals(cmd)) {
            int len = (args.length - 3) / 2;
            String[] keys = new String[len];
            for (int i = 0; i < len; i++) {
                keys[i] = args[4 + i * 2];
            }
            integer(ctx, CACHE.del(keys));
        } else if ("EXISTS".equals(cmd)) {
            int len = (args.length - 3) / 2;
            String[] keys = new String[len];
            for (int i = 0; i < len; i++) {
                keys[i] = args[4 + i * 2];
            }
            integer(ctx, CACHE.exists(keys));
        } else if ("MGET".equals(cmd)) {
            int len = (args.length - 3) / 2;
            String[] keys = new String[len];
            for (int i = 0; i < len; i++) {
                keys[i] = args[4 + i * 2];
            }
            array(ctx, CACHE.mget(keys));
        } else if ("MSET".equals(cmd)) {
            int len = (args.length - 3) / 4;
            String[] keys = new String[len];
            String[] values = new String[len];
            for (int i = 0; i < len; i++) {
                keys[i] = args[4 + i * 4];
                values[i] = args[6 + i * 4];
            }
            CACHE.mset(keys, values);
            simpleString(ctx, OK);
        } else if ("INCR".equals(cmd)) {
            String key = args[4];
            try {
                integer(ctx, CACHE.incr(key));
            } catch (NumberFormatException e) {
                error(ctx, "NFE " + key + " value [" + CACHE.get(key) + "] is not an integer.");
            }
        } else if ("DECR".equals(cmd)) {
            String key = args[4];
            try {
                integer(ctx, CACHE.decr(key));
            } catch (NumberFormatException e) {
                error(ctx, "NFE " + key + " value [" + CACHE.get(key) + "] is not an integer.");
            }
        } else {
            simpleString(ctx, OK);
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
