package io.github.alfaio.afcache.core.command;

import io.github.alfaio.afcache.core.Command;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author LinMF
 * @since 2024/6/23
 **/
public class Commands {

    private static Map<String, Command> ALL = new LinkedHashMap<>();

    static {
        initCommand();
    }

    private static void initCommand() {
        // common command
        register(new PingCommand());
        register(new InfoCommand());
        register(new CommandCommand());

        // string
        register(new SetCommand());
        register(new GetCommand());
        register(new StrLenCommand());
        register(new DelCommand());
        register(new ExistsCommand());
        register(new IncrCommand());
        register(new DecrCommand());
        register(new MsetCommand());
        register(new MgetCommand());

        // list
        register(new LpushCommand());
        register(new RpushCommand());
        register(new LpopCommand());
        register(new RpopCommand());
        register(new LlenCommand());
        register(new LindexCommand());
        register(new LrangeCommand());
    }

    public static void register(Command command) {
        ALL.put(command.name(), command);
    }

    public static Command get(String name) {
        return ALL.get(name);
    }

    public static String[] getNames() {
        return ALL.keySet().toArray(new String[0]);
    }
}
