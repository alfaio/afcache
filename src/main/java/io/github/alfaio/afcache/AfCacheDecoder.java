package io.github.alfaio.afcache;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author LinMF
 * @since 2024/6/12
 **/
public class AfCacheDecoder extends ByteToMessageDecoder {

    AtomicLong counter = new AtomicLong();

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        System.out.println("AfCacheDecoder decodeCount: " + counter.incrementAndGet());
        if (in.readableBytes() <= 0) return;
        int count = in.readableBytes();
        int index = in.readerIndex();
        System.out.println("AfCacheDecoder count: " + count + ", index: " + index);
        byte[] bytes = new byte[count];
        in.readBytes(bytes);
        String result = new String(bytes);
        System.out.println("AfCacheDecoder ret: " + result);
        out.add(result);
    }
}
