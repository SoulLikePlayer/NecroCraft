package net.necrocraft.client.model.minion.zombie;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.necrocraft.client.renderer.entity.state.ZombieMinionRenderState;
import net.necrocraft.world.entity.minion.impl.DrownedMinion;

public class DrownedMinionModel extends ZombieMinionModel<ZombieMinionRenderState> {
    private static final float PRE_DASH_TICKS = 12f;
    private static final float POST_DASH_TICKS = 10f;

    private static final float DASH_JITTER_AMP = 0.18F;

    public DrownedMinionModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer(CubeDeformation g) {
        MeshDefinition mesh = HumanoidModel.createMesh(g, 0.0F);
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(32, 48).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, g), PartPose.offset(5.0F, 2.0F, 0.0F));
        root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(16, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, g), PartPose.offset(1.9F, 12.0F, 0.0F));
        return LayerDefinition.create(mesh, 64, 64);
    }

    public void setupAnim(ZombieMinionRenderState state) {
        super.setupAnim(state);
        if (state.leftArmPose == ArmPose.THROW_TRIDENT) {
            this.leftArm.xRot = this.leftArm.xRot * 0.5F - (float) Math.PI;
            this.leftArm.yRot = 0.0F;
        }

        if (state.rightArmPose == ArmPose.THROW_TRIDENT) {
            this.rightArm.xRot = this.rightArm.xRot * 0.5F - (float) Math.PI;
            this.rightArm.yRot = 0.0F;
        }

        float swimAmount = state.swimAmount;
        if (swimAmount > 0.0F) {
            applySwimAnimation(state, swimAmount);
        }

        byte dashPhase = state.dashPhase;
        if (dashPhase != DrownedMinion.DASH_PHASE_IDLE) {
            applyDashAnimation(state, dashPhase);
        }
    }

    private void applySwimAnimation(ZombieMinionRenderState state, float swimAmount) {
        this.rightArm.xRot = Mth.rotLerpRad(swimAmount, this.rightArm.xRot, -2.5132742F)
                + swimAmount * 0.35F * Mth.sin((double) (0.1F * state.ageInTicks));
        this.leftArm.xRot = Mth.rotLerpRad(swimAmount, this.leftArm.xRot, -2.5132742F)
                - swimAmount * 0.35F * Mth.sin((double) (0.1F * state.ageInTicks));
        this.rightArm.zRot = Mth.rotLerpRad(swimAmount, this.rightArm.zRot, -0.15F);
        this.leftArm.zRot = Mth.rotLerpRad(swimAmount, this.leftArm.zRot, 0.15F);
        this.leftLeg.xRot -= swimAmount * 0.55F * Mth.sin((double) (0.1F * state.ageInTicks));
        this.rightLeg.xRot += swimAmount * 0.55F * Mth.sin((double) (0.1F * state.ageInTicks));
        this.head.xRot = 0.0F;
    }

    private void applyDashAnimation(ZombieMinionRenderState state, byte phase) {
        switch (phase) {
            case DrownedMinion.DASH_PHASE_PRE_DASH -> applyPreDashLean(state);
            case DrownedMinion.DASH_PHASE_DASHING -> applyRiptideSpin(state);
            case DrownedMinion.DASH_PHASE_POST_DASH -> applyPostDashRecovery(state);
        }
    }

    private void applyPreDashLean(ZombieMinionRenderState state) {
        final float lerpSpeed = 0.55F;
        final float age = state.ageInTicks;

        float windUp = Mth.sin(age * 1.4F) * 0.07F;

        final float targetLean = 1.05F;
        this.body.xRot = Mth.rotLerpRad(lerpSpeed, this.body.xRot, targetLean + windUp);
        this.body.yRot = Mth.rotLerpRad(lerpSpeed, this.body.yRot, windUp * 0.5F);
        this.head.xRot = Mth.rotLerpRad(lerpSpeed, this.head.xRot, targetLean * 0.55F);
        this.head.yRot = Mth.rotLerpRad(lerpSpeed, this.head.yRot, -windUp * 0.3F);

        final float armForward = -(float) Math.PI * 0.9F;
        this.rightArm.xRot = Mth.rotLerpRad(lerpSpeed, this.rightArm.xRot, armForward + windUp * 0.4F);
        this.leftArm.xRot = Mth.rotLerpRad(lerpSpeed, this.leftArm.xRot, armForward - windUp * 0.4F);
        this.rightArm.yRot = Mth.rotLerpRad(lerpSpeed, this.rightArm.yRot, 0.2F);
        this.leftArm.yRot = Mth.rotLerpRad(lerpSpeed, this.leftArm.yRot, -0.2F);
        this.rightArm.zRot = Mth.rotLerpRad(lerpSpeed, this.rightArm.zRot, 0.1F);
        this.leftArm.zRot = Mth.rotLerpRad(lerpSpeed, this.leftArm.zRot, -0.1F);

        this.rightLeg.xRot = Mth.rotLerpRad(lerpSpeed, this.rightLeg.xRot, 0.55F);
        this.leftLeg.xRot = Mth.rotLerpRad(lerpSpeed, this.leftLeg.xRot, 0.55F);
        this.rightLeg.zRot = Mth.rotLerpRad(lerpSpeed, this.rightLeg.zRot, -0.15F);
        this.leftLeg.zRot = Mth.rotLerpRad(lerpSpeed, this.leftLeg.zRot, 0.15F);
    }

    private void applyRiptideSpin(ZombieMinionRenderState state) {
        float age = state.ageInTicks;


        float spinAngle = -age * 1.8849556F;
        this.body.yRot = spinAngle;
        this.head.yRot = spinAngle;
        this.rightArm.yRot = spinAngle;
        this.leftArm.yRot = spinAngle;
        this.rightLeg.yRot = spinAngle;
        this.leftLeg.yRot = spinAngle;

        this.body.xRot = 0.6F;
        this.head.xRot = 0.3F;

        this.rightArm.xRot = -(float) Math.PI * 0.7F;
        this.rightArm.zRot = -(float) Math.PI * 0.05F;
        this.leftArm.xRot = -(float) Math.PI * 0.6F;
        this.leftArm.zRot = (float) Math.PI * 0.05F;

        this.rightLeg.xRot = 0.35F;
        this.leftLeg.xRot = 0.35F;
        this.rightLeg.zRot = -0.1F;
        this.leftLeg.zRot = 0.1F;

        float jitter = DASH_JITTER_AMP;
        float freq = 3.5F;

        float jRA = jitter * Mth.sin(age * freq);
        float jLA = jitter * Mth.sin(age * freq + Mth.PI * 0.5F);
        float jRL = jitter * Mth.sin(age * freq + Mth.PI);
        float jLL = jitter * Mth.sin(age * freq + Mth.PI * 1.5F);

        this.rightArm.xRot += jRA;
        this.rightArm.zRot += jRA * 0.4F;
        this.leftArm.xRot += jLA;
        this.leftArm.zRot += jLA * 0.4F;
        this.rightLeg.xRot += jRL;
        this.rightLeg.zRot += jRL * 0.3F;
        this.leftLeg.xRot += jLL;
        this.leftLeg.zRot += jLL * 0.3F;

        this.head.xRot += jitter * 0.5F * Mth.sin(age * freq + Mth.PI * 0.25F);
    }

    private void applyPostDashRecovery(ZombieMinionRenderState state) {
        final float easeSpeed = 0.22F;
        final float age = state.ageInTicks;

        float bodyEnvelope = Mth.clamp(Math.abs(this.body.xRot) + Math.abs(this.body.yRot), 0.0F, 1.0F);
        float overshoot = Mth.sin(age * 2.2F) * 0.08F * bodyEnvelope;

        this.body.xRot = Mth.rotLerpRad(easeSpeed, this.body.xRot, overshoot);
        this.body.yRot = Mth.rotLerpRad(easeSpeed, this.body.yRot, overshoot * 0.5F);
        this.head.xRot = Mth.rotLerpRad(easeSpeed, this.head.xRot, overshoot * 0.6F);
        this.head.yRot = Mth.rotLerpRad(easeSpeed, this.head.yRot, 0.0F);

        this.rightArm.xRot = Mth.rotLerpRad(easeSpeed, this.rightArm.xRot, overshoot * 0.4F);
        this.rightArm.yRot = Mth.rotLerpRad(easeSpeed, this.rightArm.yRot, 0.0F);
        this.rightArm.zRot = Mth.rotLerpRad(easeSpeed, this.rightArm.zRot, 0.0F);
        this.leftArm.xRot = Mth.rotLerpRad(easeSpeed, this.leftArm.xRot, -overshoot * 0.4F);
        this.leftArm.yRot = Mth.rotLerpRad(easeSpeed, this.leftArm.yRot, 0.0F);
        this.leftArm.zRot = Mth.rotLerpRad(easeSpeed, this.leftArm.zRot, 0.0F);

        if (state.swimAmount <= 0.0F) {
            this.rightLeg.xRot = Mth.rotLerpRad(easeSpeed, this.rightLeg.xRot, 0.0F);
            this.leftLeg.xRot = Mth.rotLerpRad(easeSpeed, this.leftLeg.xRot, 0.0F);
        }
        this.rightLeg.zRot = Mth.rotLerpRad(easeSpeed, this.rightLeg.zRot, 0.0F);
        this.leftLeg.zRot = Mth.rotLerpRad(easeSpeed, this.leftLeg.zRot, 0.0F);
    }
}