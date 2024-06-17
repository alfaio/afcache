package io.github.alfaio.afcache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author LinMF
 * @since 2024/6/12
 **/
@Component
public class AfApplicationListener implements ApplicationListener<ApplicationEvent> {

    @Autowired
    List<AfPlugin> plugins;

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ApplicationReadyEvent) {
            for (AfPlugin plugin : plugins) {
                plugin.init();
                plugin.startup();
            }
        } else if (event instanceof ContextClosedEvent) {
            for (AfPlugin plugin : plugins) {
                plugin.shutdown();
            }
        }
    }
}
