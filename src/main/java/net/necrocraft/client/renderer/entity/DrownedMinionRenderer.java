package net.necrocraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.necrocraft.client.model.minion.zombie.DrownedMinionModel;
import net.necrocraft.client.renderer.entity.layers.DrownedMinionOuterLayer;
import net.necrocraft.client.renderer.entity.state.ZombieMinionRenderState;
import net.necrocraft.world.entity.minion.impl.DrownedMinion;

public class DrownedMinionRenderer  extends AbstractZombieMinionRenderer<DrownedMinion, ZombieMinionRenderState, DrownedMinionModel> {
    private static final Identifier DROWNED_LOCATION = Identifier.withDefaultNamespace("textures/entity/zombie/drowned.png");

    public DrownedMinionRenderer(EntityRendererProvider.Context context) {
        super(context, new DrownedMinionModel(context.bakeLayer(ModelLayers.DROWNED)), ArmorModelSet.bake(ModelLayers.DROWNED_ARMOR, context.getModelSet(), DrownedMinionModel::new));
        this.addLayer(new DrownedMinionOuterLayer(this, context.getModelSet()));
    }

    public ZombieMinionRenderState createRenderState() {
        return new ZombieMinionRenderState();
    }

    public Identifier getTextureLocation(ZombieMinionRenderState state) {
        return DROWNED_LOCATION;
    }

    protected void setupRotations(ZombieMinionRenderState state, PoseStack poseStack, float bodyRot, float entityScale) {
        super.setupRotations(state, poseStack, bodyRot, entityScale);
        float swimAmount = state.swimAmount;
        if (swimAmount > 0.0F) {
            float targetRotationX = -10.0F - state.xRot;
            float rotationX = Mth.lerp(swimAmount, 0.0F, targetRotationX);
            poseStack.rotateAround(Axis.XP.rotationDegrees(rotationX), 0.0F, state.boundingBoxHeight / 2.0F / entityScale, 0.0F);
        }

    }

    protected HumanoidModel.ArmPose getArmPose(DrownedMinion mob, HumanoidArm arm) {
        ItemStack item = mob.getItemHeldByArm(arm);
        return mob.getMainArm() == arm && mob.isAggressive() && item.is(Items.TRIDENT) ? HumanoidModel.ArmPose.THROW_TRIDENT : super.getArmPose(mob, arm);
    }
}
