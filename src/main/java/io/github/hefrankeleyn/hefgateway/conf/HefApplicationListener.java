package io.github.hefrankeleyn.hefgateway.conf;

import io.github.hefrankeleyn.hefgateway.plugins.HefPlugin;
import jakarta.annotation.Resource;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * @Date 2024/6/29
 * @Author lifei
 */
@Component
public class HefApplicationListener implements ApplicationListener<ApplicationEvent> {

    @Resource
    private List<HefPlugin> hefPluginList;

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ApplicationReadyEvent readyEvent) {
            if (Objects.nonNull(hefPluginList)) {
                for (HefPlugin hefPlugin : hefPluginList) {
                    hefPlugin.init();
                    hefPlugin.startup();
                }
            }
        } else if (event instanceof ContextClosedEvent closedEvent) {
            if (Objects.nonNull(hefPluginList)) {
                for (HefPlugin hefPlugin : hefPluginList) {
                    hefPlugin.shutdown();
                }
            }
        }
    }
}
