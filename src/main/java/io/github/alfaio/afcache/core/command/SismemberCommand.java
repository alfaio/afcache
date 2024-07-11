package io.github.alfaio.afcache.core.command;

import io.github.alfaio.afcache.core.AfCache;
import io.github.alfaio.afcache.core.Command;
import io.github.alfaio.afcache.core.Reply;

/**
 * @author LinMF
 * @since 2024/6/23
 **/
public class SismemberCommand implements Command {

    @Override
    public String name() {
        return "SISMEMBER";
    }

    @Override
    public Reply<?> exec(AfCache cache, String[] args) {
        String key = getKey(args);
        String val = getVal(args);
        return Reply.integer(cache.sismember(key, val));
    }
}
