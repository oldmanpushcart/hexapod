package com.github.oldmanpushcart.hexapod.platform.test;

import com.github.oldmanpushcart.hexapod.platform.gait.Gait;
import com.github.oldmanpushcart.hexapod.platform.gait.Limb;
import io.github.athingx.athing.standard.platform.message.ThingReplyServiceReturnMessage;
import io.github.athingx.athing.standard.platform.op.OpReturn;
import io.github.athingx.athing.standard.platform.op.OpReturnHelper;
import io.github.oldmanpushcart.hexapod.api.Posture;
import io.github.oldmanpushcart.hexapod.api.PostureThingCom;
import org.junit.Assert;
import org.junit.Test;

import static com.github.oldmanpushcart.hexapod.platform.util.HexMath.fraction;
import static io.github.oldmanpushcart.hexapod.api.Joint.*;
import static java.lang.Math.PI;

public class HexPostureCompTestCase extends ThingPlatformSupport {


    @Test
    public void test$debug$stand() throws Exception {

        final PostureThingCom postureComp = platform.getThingTemplate(PRODUCT_ID, THING_ID)
                .getThingCom("posture", PostureThingCom.class);

        final Limb[] ALL = Limb.values();
        final Limb[] S1 = {Limb.L_F, Limb.R_M, Limb.L_H};
        final Limb[] S2 = {Limb.R_F, Limb.L_M, Limb.R_H};

        final OpReturn<Void> opReturn = OpReturnHelper.getOpEmptyReturn(() ->
                postureComp.change(
                        new Posture[]{
                                new Posture(1500)
                                        .put(L_F_H, fraction(PI, 10, 20)) // 90.00
                                        .put(L_F_K, fraction(PI, 10, 20)) // 90.00
                                        .put(L_F_A, fraction(PI,  5, 20)) // 135.00
                                        .put(L_M_H, fraction(PI, 10, 20)) // 90.00
                                        .put(L_M_K, fraction(PI, 10, 20)) // 90.00
                                        .put(L_M_A, fraction(PI,  5, 20)) // 135.00
                                        .put(L_H_H, fraction(PI, 10, 20)) // 90.00
                                        .put(L_H_K, fraction(PI, 10, 20)) // 90.00
                                        .put(L_H_A, fraction(PI,  5, 20)) // 135.00
                                        .put(R_F_H, fraction(PI, 10, 20)) // 90.00
                                        .put(R_F_K, fraction(PI, 10, 20)) // 90.00
                                        .put(R_F_A, fraction(PI, 15, 20)) // 135.00
                                        .put(R_M_H, fraction(PI, 10, 20)) // 90.00
                                        .put(R_M_K, fraction(PI, 10, 20)) // 90.00
                                        .put(R_M_A, fraction(PI, 15, 20)) // 135.00
                                        .put(R_H_H, fraction(PI, 10, 20)) // 90.00
                                        .put(R_H_K, fraction(PI, 10, 20)) // 90.00
                                        .put(R_H_A, fraction(PI, 15, 20)) // 135.00
                        }
                ));
        final ThingReplyServiceReturnMessage message = waitingForReplyMessageByToken(opReturn.getToken());
        Assert.assertEquals(200, message.getCode());

    }


    @Test
    public void test$debug$f() throws Exception {

        final PostureThingCom postureComp = platform.getThingTemplate(PRODUCT_ID, THING_ID)
                .getThingCom("posture", PostureThingCom.class);

        final Limb[] ALL = Limb.values();
        final Limb[] S1 = {Limb.L_F, Limb.R_M, Limb.L_H};
        final Limb[] S2 = {Limb.R_F, Limb.L_M, Limb.R_H};

        final OpReturn<Void> opReturn = OpReturnHelper.getOpEmptyReturn(() ->
                postureComp.change(
                        new Posture[]{
                                new Posture(150)
                                        .put(L_F_H, fraction(PI, 12, 20)) // 108.00
                                        .put(L_F_K, fraction(PI, 10, 20)) // 90.00
                                        .put(L_F_A, fraction(PI,  5, 20)) // 45.00
                                        .put(R_M_H, fraction(PI,  8, 20)) // 108.00
                                        .put(R_M_K, fraction(PI, 10, 20)) // 90.00
                                        .put(R_M_A, fraction(PI, 15, 20)) // 45.00
                                        .put(L_H_H, fraction(PI, 12, 20)) // 108.00
                                        .put(L_H_K, fraction(PI, 10, 20)) // 90.00
                                        .put(L_H_A, fraction(PI,  5, 20)) // 45.00
                                        .put(R_F_H, fraction(PI, 12, 20)) // 72.00
                                        .put(R_F_K, fraction(PI, 12, 20)) // 72.00
                                        .put(R_F_A, fraction(PI, 15, 20)) // 45.00
                                        .put(L_M_H, fraction(PI,  8, 20)) // 72.00
                                        .put(L_M_K, fraction(PI,  8, 20)) // 72.00
                                        .put(L_M_A, fraction(PI,  5, 20)) // 45.00
                                        .put(R_H_H, fraction(PI, 12, 20)) // 72.00
                                        .put(R_H_K, fraction(PI, 12, 20)) // 72.00
                                        .put(R_H_A, fraction(PI, 15, 20)), // 45.00,
                                new Posture(100)
                                        .put(L_F_H, fraction(PI,  8, 20)) // 72.00
                                        .put(L_F_K, fraction(PI,  8, 20)) // 72.00
                                        .put(L_F_A, fraction(PI,  5, 20)) // 45.00
                                        .put(R_M_H, fraction(PI, 12, 20)) // 72.00
                                        .put(R_M_K, fraction(PI, 12, 20)) // 72.00
                                        .put(R_M_A, fraction(PI, 15, 20)) // 45.00
                                        .put(L_H_H, fraction(PI,  8, 20)) // 72.00
                                        .put(L_H_K, fraction(PI,  8, 20)) // 72.00
                                        .put(L_H_A, fraction(PI,  5, 20)) // 45.00
                                        .put(R_F_H, fraction(PI,  8, 20)) // 108.00
                                        .put(R_F_K, fraction(PI, 10, 20)) // 90.00
                                        .put(R_F_A, fraction(PI, 15, 20)) // 45.00
                                        .put(L_M_H, fraction(PI, 12, 20)) // 108.00
                                        .put(L_M_K, fraction(PI, 10, 20)) // 90.00
                                        .put(L_M_A, fraction(PI,  5, 20)) // 45.00
                                        .put(R_H_H, fraction(PI,  8, 20)) // 108.00
                                        .put(R_H_K, fraction(PI, 10, 20)) // 90.00
                                        .put(R_H_A, fraction(PI, 15, 20)), // 45.00
                                new Posture(150)
                                        .put(L_F_H, fraction(PI,  8, 20)) // 72.00
                                        .put(L_F_K, fraction(PI,  8, 20)) // 72.00
                                        .put(L_F_A, fraction(PI,  5, 20)) // 45.00
                                        .put(R_M_H, fraction(PI, 12, 20)) // 72.00
                                        .put(R_M_K, fraction(PI, 12, 20)) // 72.00
                                        .put(R_M_A, fraction(PI, 15, 20)) // 45.00
                                        .put(L_H_H, fraction(PI,  8, 20)) // 72.00
                                        .put(L_H_K, fraction(PI,  8, 20)) // 72.00
                                        .put(L_H_A, fraction(PI,  5, 20)) // 45.00
                                        .put(R_F_H, fraction(PI,  8, 20)) // 108.00
                                        .put(R_F_K, fraction(PI, 10, 20)) // 90.00
                                        .put(R_F_A, fraction(PI, 15, 20)) // 45.00
                                        .put(L_M_H, fraction(PI, 12, 20)) // 108.00
                                        .put(L_M_K, fraction(PI, 10, 20)) // 90.00
                                        .put(L_M_A, fraction(PI,  5, 20)) // 45.00
                                        .put(R_H_H, fraction(PI,  8, 20)) // 108.00
                                        .put(R_H_K, fraction(PI, 10, 20)) // 90.00
                                        .put(R_H_A, fraction(PI, 15, 20)), // 45.00,
                                new Posture(100)
                                        .put(L_F_H, fraction(PI,  8, 20)) // 72.00
                                        .put(L_F_K, fraction(PI, 10, 20)) // 90.00
                                        .put(L_F_A, fraction(PI,  5, 20)) // 45.00
                                        .put(R_M_H, fraction(PI, 12, 20)) // 72.00
                                        .put(R_M_K, fraction(PI, 10, 20)) // 90.00
                                        .put(R_M_A, fraction(PI, 15, 20)) // 45.00
                                        .put(L_H_H, fraction(PI,  8, 20)) // 72.00
                                        .put(L_H_K, fraction(PI, 10, 20)) // 90.00
                                        .put(L_H_A, fraction(PI,  5, 20)) // 45.00

                                        .put(R_F_H, fraction(PI,  8, 20)) // 108.00
                                        .put(R_F_K, fraction(PI, 10, 20)) // 90.00
                                        .put(R_F_A, fraction(PI, 15, 20)) // 45.00
                                        .put(L_M_H, fraction(PI, 12, 20)) // 108.00
                                        .put(L_M_K, fraction(PI, 10, 20)) // 90.00
                                        .put(L_M_A, fraction(PI,  5, 20)) // 45.00
                                        .put(R_H_H, fraction(PI,  8, 20)) // 108.00
                                        .put(R_H_K, fraction(PI, 10, 20)) // 90.00
                                        .put(R_H_A, fraction(PI, 15, 20)), // 45.00

                        }
                ));
        final ThingReplyServiceReturnMessage message = waitingForReplyMessageByToken(opReturn.getToken());
        Assert.assertEquals(200, message.getCode());

    }




    @Test
    public void test() throws Exception {

        final PostureThingCom postureComp = platform.getThingTemplate(PRODUCT_ID, THING_ID)
                .getThingCom("posture", PostureThingCom.class);

        final Limb[] ALL = Limb.values();
        final Limb[] S1 = {Limb.L_F, Limb.R_M, Limb.L_H};
        final Limb[] S2 = {Limb.R_F, Limb.L_M, Limb.R_H};

        final OpReturn<Void> opReturn = OpReturnHelper.getOpEmptyReturn(() ->
                postureComp.change(
                        Gait.stand(150)
                                .next(Gait.moveForward(150, 4))
                                .next(Gait.moveBackward(150,4))
                                .toPostures()
                ));
        final ThingReplyServiceReturnMessage message = waitingForReplyMessageByToken(opReturn.getToken());
        Assert.assertEquals(200, message.getCode());

    }

}
