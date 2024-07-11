package io.github.alfaio.afcache.core.command;

import io.github.alfaio.afcache.core.AfCache;
import io.github.alfaio.afcache.core.Command;
import io.github.alfaio.afcache.core.Reply;

/**
 * @author LinMF
 * @since 2024/6/24
 **/
public class ScardCommand implements Command {

    @Override
    public String name() {
        return "SCARD";
    }

    @Override
    public Reply<?> exec(AfCache cache, String[] args) {
        String key = getKey(args);
        return Reply.integer(cache.scard(key));
    }
}
