package io.github.oldmanpushcart.hexapod.thing.component;

import com.google.inject.Inject;
import io.github.oldmanpushcart.hexapod.thing.manager.PostureMgr;
import io.github.oldmanpushcart.jpromisor.ListenableFuture;
import io.github.oldmanpushcart.hexapod.api.Posture;
import io.github.oldmanpushcart.hexapod.api.PostureThingCom;

public class HexPostureComp implements PostureThingCom {

    @Inject
    private PostureMgr postureMgr;

    @Override
    public void interrupt() {
        postureMgr.interrupt();
    }

    @Override
    public ListenableFuture<Void> change(Posture[] postures) {
        return postureMgr.change(postures);
    }

}
