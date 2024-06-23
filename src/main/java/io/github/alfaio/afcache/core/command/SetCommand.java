package io.github.alfaio.afcache.core.command;

import io.github.alfaio.afcache.core.AfCache;
import io.github.alfaio.afcache.core.Command;
import io.github.alfaio.afcache.core.Reply;

/**
 * @author LinMF
 * @since 2024/6/23
 **/
public class SetCommand implements Command {

    @Override
    public String name() {
        return "SET";
    }

    @Override
    public Reply<?> exec(AfCache cache, String[] args) {
        cache.set(getKey(args), getVal(args));
        return Reply.string(OK);
    }

}
