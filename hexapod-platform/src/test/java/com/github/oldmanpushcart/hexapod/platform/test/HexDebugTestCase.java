package com.github.oldmanpushcart.hexapod.platform.test;

import com.github.oldmanpushcart.hexapod.platform.gait.Gait;
import io.github.athingx.athing.standard.platform.message.ThingReplyServiceReturnMessage;
import io.github.athingx.athing.standard.platform.op.OpReturn;
import io.github.athingx.athing.standard.platform.op.OpReturnHelper;
import io.github.oldmanpushcart.hexapod.api.PostureThingCom;
import org.junit.Assert;
import org.junit.Test;

import static com.github.oldmanpushcart.hexapod.platform.gait.Gait.*;

/**
 * 六足调试测试用例
 */
public class HexDebugTestCase extends ThingPlatformSupport {

    @Test
    public void test$debug() throws Exception {

        final PostureThingCom postureComp = platform.getThingTemplate(PRODUCT_ID, THING_ID)
                .getThingCom("posture", PostureThingCom.class);

        final OpReturn<Void> opReturn = OpReturnHelper.getOpEmptyReturn(() ->
                postureComp.change(
                        Gait.stand(150)
                                .next(moveForward(250, 8))
                                .next(moveBackward(250, 8))
                                .next(turnLeft(150, 4))
                                .next(turnRight(150, 4))
                                .toPostures()
                ));
        final ThingReplyServiceReturnMessage message = waitingForReplyMessageByToken(opReturn.getToken());
        Assert.assertEquals(200, message.getCode());

    }

}
