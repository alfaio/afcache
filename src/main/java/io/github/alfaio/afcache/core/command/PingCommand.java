package io.github.alfaio.afcache.core.command;

import io.github.alfaio.afcache.core.AfCache;
import io.github.alfaio.afcache.core.Command;
import io.github.alfaio.afcache.core.Reply;

/**
 * @author LinMF
 * @since 2024/6/23
 **/
public class PingCommand implements Command {

    @Override
    public String name() {
        return "PING";
    }

    @Override
    public Reply<?> exec(AfCache cache, String[] args) {
        String result = "PONG";
        if (args.length >= 5) {
            result = args[4];
        }
        return Reply.string(result);
    }

}
