package io.github.hefrankeleyn.hefgateway.plugins;

public interface HefPlugin {

    void init();
    void startup();
    void shutdown();
}
