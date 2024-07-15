package io.github.alfaio.afcache.core.command;

import io.github.alfaio.afcache.core.AfCache;
import io.github.alfaio.afcache.core.Command;
import io.github.alfaio.afcache.core.Reply;

import java.util.Arrays;

/**
 * @author LinMF
 * @since 2024/6/23
 **/
public class ZremCommand implements Command {

    @Override
    public String name() {
        return "ZREM";
    }

    @Override
    public Reply<?> exec(AfCache cache, String[] args) {
        String key = getKey(args);
        String[] vals = getParamsNoKey(args);
        return Reply.integer(cache.zrem(key, vals));
    }

}
