package io.github.alfaio.afcache;

/**
 * @author LinMF
 * @since 2024/6/12
 **/
public interface AfPlugin {

    void init();

    void startup();

    void shutdown();
}
