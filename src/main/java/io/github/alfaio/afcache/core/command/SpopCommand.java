package io.github.alfaio.afcache.core.command;

import io.github.alfaio.afcache.core.AfCache;
import io.github.alfaio.afcache.core.Command;
import io.github.alfaio.afcache.core.Reply;

/**
 * @author LinMF
 * @since 2024/6/23
 **/
public class SpopCommand implements Command {

    @Override
    public String name() {
        return "SPOP";
    }

    @Override
    public Reply<?> exec(AfCache cache, String[] args) {
        String key = getKey(args);
        int count = 1;
        if (args.length > 6) {
            String val = getVal(args);
            count = Integer.parseInt(val);
            return Reply.array(cache.spop(key, count));
        }
        String[] spop = cache.spop(key, count);
        return Reply.bulkString(spop == null ? null : spop[0]);
    }
}
