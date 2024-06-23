package io.github.alfaio.afcache.core;

/**
 * @author LinMF
 * @since 2024/6/23
 **/
public interface Command {

    String CRLF = "\r\n";
    String OK = "OK";

    String name();

    Reply<?> exec(AfCache cache, String[] args);

    default String getKey(String[] args) {
        return args[4];
    }

    default String getVal(String[] args) {
        return args[6];
    }

    default String[] getParams(String[] args) {
        int len = (args.length - 3) / 2;
        String[] keys = new String[len];
        for (int i = 0; i < len; i++) {
            keys[i] = args[4 + i * 2];
        }
        return keys;
    }

    default String[] getParamsNoKey(String[] args) {
        int len = (args.length - 5) / 2;
        String[] keys = new String[len];
        for (int i = 0; i < len; i++) {
            keys[i] = args[6 + i * 2];
        }
        return keys;
    }

    default String[] getKeys(String[] args) {
        int len = (args.length - 3) / 4;
        String[] keys = new String[len];
        for (int i = 0; i < len; i++) {
            keys[i] = args[4 + i * 4];
        }
        return keys;
    }

    default String[] getVals(String[] args) {
        int len = (args.length - 3) / 4;
        String[] values = new String[len];
        for (int i = 0; i < len; i++) {
            values[i] = args[6 + i * 4];
        }
        return values;
    }
}
