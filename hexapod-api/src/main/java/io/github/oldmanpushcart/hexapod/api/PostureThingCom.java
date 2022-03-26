package io.github.oldmanpushcart.hexapod.api;

import io.github.athingx.athing.standard.component.ThingCom;
import io.github.athingx.athing.standard.component.annotation.ThCom;
import io.github.athingx.athing.standard.component.annotation.ThParam;
import io.github.athingx.athing.standard.component.annotation.ThService;
import io.github.oldmanpushcart.jpromisor.ListenableFuture;

/**
 * 机器人姿态组件
 */
@ThCom(id = "posture", name = "姿态变更组件", desc = "控制机器人姿态")
public interface PostureThingCom extends ThingCom {

    @ThService(name = "中断姿态变更", desc = "中断当前姿态变更动作，并清空等待中的姿态变更请求")
    void interrupt();

    /**
     * 改变姿态
     *
     * @param postures 机器人姿态
     * @return Future
     */
    @ThService(isSync = false, name = "姿态变更", desc = "机器人姿态")
    ListenableFuture<Void> change(@ThParam("postures") Posture[] postures);

}
