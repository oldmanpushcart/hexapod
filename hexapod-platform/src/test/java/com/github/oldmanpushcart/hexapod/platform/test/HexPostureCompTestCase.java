package com.github.oldmanpushcart.hexapod.platform.test;

import io.github.athingx.athing.standard.platform.message.ThingReplyPropertySetMessage;
import io.github.athingx.athing.standard.platform.message.ThingReplyServiceReturnMessage;
import io.github.athingx.athing.standard.platform.op.OpReturn;
import io.github.athingx.athing.standard.platform.op.OpReturnHelper;
import io.github.oldmanpushcart.hexapod.api.Joint;
import io.github.oldmanpushcart.hexapod.api.Posture;
import io.github.oldmanpushcart.hexapod.api.PostureThingCom;
import org.junit.Assert;
import org.junit.Test;

public class HexPostureCompTestCase extends ThingPlatformSupport {

    @Test
    public void test() throws Exception {

        final PostureThingCom postureComp = platform.getThingTemplate(PRODUCT_ID, THING_ID)
                .getThingCom("posture", PostureThingCom.class);

        while (true) {
            final OpReturn<Void> opReturn = OpReturnHelper.getOpEmptyReturn(() ->
                    postureComp.change(new Posture[]{
                            new Posture(500).map(Joint.values(), Math.PI / 2),
                            new Posture(500)
                                    .map(Joint.select().is(Joint.right).not(Joint.hip).selected(), Math.PI / 6 * 5)
                                    .map(Joint.select().is(Joint.left).not(Joint.hip).selected(), Math.PI - Math.PI / 6 * 5)
                    })
            );

            final ThingReplyServiceReturnMessage message = waitingForReplyMessageByToken(opReturn.getToken());
            Assert.assertEquals(200, message.getCode());
        }


    }

}
