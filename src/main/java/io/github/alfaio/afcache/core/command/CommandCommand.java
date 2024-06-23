package io.github.alfaio.afcache.core.command;

import io.github.alfaio.afcache.core.AfCache;
import io.github.alfaio.afcache.core.Command;
import io.github.alfaio.afcache.core.Reply;

/**
 * @author LinMF
 * @since 2024/6/23
 **/
public class CommandCommand implements Command {

    @Override
    public String name() {
        return "COMMAND";
    }

    @Override
    public Reply<?> exec(AfCache cache, String[] args) {
        return Reply.array(Commands.getNames());
    }

}
