package io.github.alfaio.afcache.core.command;

import io.github.alfaio.afcache.core.AfCache;
import io.github.alfaio.afcache.core.Command;
import io.github.alfaio.afcache.core.Reply;

/**
 * @author LinMF
 * @since 2024/6/23
 **/
public class SmembersCommand implements Command {

    @Override
    public String name() {
        return "SMEMBERS";
    }

    @Override
    public Reply<?> exec(AfCache cache, String[] args) {
        String key = getKey(args);
        return Reply.array(cache.smembers(key));
    }
}
