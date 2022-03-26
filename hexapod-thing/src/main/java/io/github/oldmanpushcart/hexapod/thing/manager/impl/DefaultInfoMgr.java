package io.github.oldmanpushcart.hexapod.thing.manager.impl;

import com.google.inject.Singleton;
import io.github.oldmanpushcart.hexapod.thing.manager.InfoMgr;

@Singleton
public class DefaultInfoMgr implements InfoMgr {

    private final String name;

    public DefaultInfoMgr(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

}
