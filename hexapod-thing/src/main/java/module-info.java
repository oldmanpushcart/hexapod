module io.github.oldmanpushcart.hexpod.thing {
    requires io.github.oldmanpushcart.hexpod.api;
    requires io.github.athingx.athing.aliyun.thing;
    requires io.github.athingx.athing.thing.io;
    requires io.github.athingx.athing.thing.monitor;
    requires org.slf4j;
    requires com.google.guice;
    requires jopt.simple;
    requires org.apache.commons.lang3;

    uses io.github.athingx.athing.standard.thing.boot.ThingBoot;
    opens io.github.oldmanpushcart.hexapod.thing.manager.impl to io.github.athingx.athing.aliyun.thing;
    opens io.github.oldmanpushcart.hexapod.thing to io.github.athingx.athing.aliyun.thing;
    opens io.github.oldmanpushcart.hexapod.thing.component to io.github.athingx.athing.aliyun.thing;

}