package com.github.oldmanpushcart.hexapod.platform.test.message;


import io.github.athingx.athing.standard.platform.message.ThingMessage;
import io.github.athingx.athing.standard.platform.message.ThingMessageListener;

public class QaThingMessageGroupListener implements ThingMessageListener {

    private final ThingMessageListener[] group;

    public QaThingMessageGroupListener(ThingMessageListener[] group) {
        this.group = group;
    }

    @Override
    public void onMessage(ThingMessage message) throws Exception {
        for (ThingMessageListener listener : group) {
            listener.onMessage(message);
        }
    }

}
