package io.github.oldmanpushcart.hexapod.thing.manager;

import io.github.oldmanpushcart.jpromisor.ListenableFuture;
import io.github.oldmanpushcart.hexapod.api.Posture;

public interface PostureMgr {

    void interrupt();
    ListenableFuture<Void> change(Posture[] postures);

}
