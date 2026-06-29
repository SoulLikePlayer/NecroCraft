package net.necrocraft.client.model.minion.zombie;

import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.ZombieRenderState;
import net.necrocraft.client.renderer.entity.state.ZombieMinionRenderState;

public abstract class AbstractZombieMinionModel<S extends ZombieMinionRenderState> extends HumanoidModel<S> {
    protected AbstractZombieMinionModel(ModelPart root) {
        super(root);
    }

    public void setupAnim(S state) {
        super.setupAnim(state);
        AnimationUtils.animateZombieArms(this.leftArm, this.rightArm, state.isAggressive, state);
    }
}
