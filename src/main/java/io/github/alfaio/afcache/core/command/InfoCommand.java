package io.github.alfaio.afcache.core.command;

import io.github.alfaio.afcache.core.AfCache;
import io.github.alfaio.afcache.core.Command;
import io.github.alfaio.afcache.core.Reply;

/**
 * @author LinMF
 * @since 2024/6/23
 **/
public class InfoCommand implements Command {

    public static final String INFO = "AfCache server[1.1.0], created by Alfa." + CRLF
            + "Mock redis server at 2024-06-23 in XiaMen." + CRLF;


    @Override
    public String name() {
        return "INFO";
    }

    @Override
    public Reply<?> exec(AfCache cache, String[] args) {
        return Reply.bulkString(INFO);
    }

}
