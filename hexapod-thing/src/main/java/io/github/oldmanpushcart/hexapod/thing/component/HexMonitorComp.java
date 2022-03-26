package io.github.oldmanpushcart.hexapod.thing.component;

import io.github.athingx.athing.standard.thing.ThingConnection;
import io.github.athingx.athing.standard.thing.ThingLifeCycle;
import io.github.athingx.athing.standard.thing.boot.ThInject;
import io.github.athingx.athing.thing.monitor.ThingMonitorCom;
import io.github.athingx.athing.thing.monitor.info.Info;
import io.github.athingx.athing.thing.monitor.usage.Usage;
import io.github.oldmanpushcart.hexapod.thing.util.Initializer;

import java.util.concurrent.TimeUnit;

public class HexMonitorComp implements ThingLifeCycle {

    @ThInject
    private ThingMonitorCom monitor;

    private final Initializer reportInfoInit = new Initializer();
    private final Initializer reportUsageInit = new Initializer();

    @Override
    public void onConnected(ThingConnection connection) {

        // 检查info是否已经成功上报，如未上报则必须上报成功一次
        reportInfoInit.init(() -> monitor.reportInfo(Info.Item.values()).sync());

        // 激活定时任务
        reportUsageInit.init(() -> monitor.scheduleReportUsage(3, TimeUnit.MINUTES, Usage.Item.values()));

    }

}
