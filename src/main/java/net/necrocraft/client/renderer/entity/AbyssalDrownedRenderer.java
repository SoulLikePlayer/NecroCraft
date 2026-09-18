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
import net.necrocraft.client.renderer.entity.layers.AbyssalDrownedMinionOuterLayer;
import net.necrocraft.client.renderer.entity.layers.DrownedMinionDashRiptideLayer;
import net.necrocraft.client.renderer.entity.layers.DrownedMinionOuterLayer;
import net.necrocraft.client.renderer.entity.state.ZombieMinionRenderState;
import net.necrocraft.core.NecroCraft;
import net.necrocraft.world.entity.minion.impl.drowned.AbyssalDrownedMinion;
import net.necrocraft.world.entity.minion.impl.drowned.DrownedMinion;

public class AbyssalDrownedRenderer extends AbstractZombieMinionRenderer<AbyssalDrownedMinion, ZombieMinionRenderState, DrownedMinionModel> {
    private static final Identifier ABYSSAL_DROWNED_LOCATION = Identifier.fromNamespaceAndPath(NecroCraft.MODID, "textures/entity/drowned/abyssal/abyssal_drowned.png");
    private static final Identifier NEMESIS_ABYSSAL_DROWNED_LOCATION = Identifier.fromNamespaceAndPath(NecroCraft.MODID, "textures/entity/drowned/nemesis_drowned.png");

    public AbyssalDrownedRenderer(EntityRendererProvider.Context context) {
        super(context, new DrownedMinionModel(context.bakeLayer(ModelLayers.DROWNED)), ArmorModelSet.bake(ModelLayers.DROWNED_ARMOR, context.getModelSet(), DrownedMinionModel::new));

        this.addLayer(new AbyssalDrownedMinionOuterLayer(this, context.getModelSet()));
        this.addLayer(new DrownedMinionDashRiptideLayer(this));
    }

    @Override
    public ZombieMinionRenderState createRenderState() {
        return new ZombieMinionRenderState();
    }

    @Override
    public void extractRenderState(AbyssalDrownedMinion entity, ZombieMinionRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.dashPhase = entity.getDashPhase();
    }

    @Override
    public Identifier getTextureLocation(ZombieMinionRenderState state) {
        return state.isNemesis ? NEMESIS_ABYSSAL_DROWNED_LOCATION : ABYSSAL_DROWNED_LOCATION;
    }

    @Override
    protected void setupRotations(ZombieMinionRenderState state, PoseStack poseStack, float bodyRot, float entityScale) {
        super.setupRotations(state, poseStack, bodyRot, entityScale);
        float swimAmount = state.swimAmount;
        if (swimAmount > 0.0F) {
            float targetRotationX = -10.0F - state.xRot;
            float rotationX = Mth.lerp(swimAmount, 0.0F, targetRotationX);
            poseStack.rotateAround(Axis.XP.rotationDegrees(rotationX), 0.0F, state.boundingBoxHeight / 2.0F / entityScale, 0.0F);
        }
    }

    @Override
    protected HumanoidModel.ArmPose getArmPose(AbyssalDrownedMinion mob, HumanoidArm arm) {
        ItemStack item = mob.getItemHeldByArm(arm);
        return mob.getMainArm() == arm && mob.isAggressive() && item.is(Items.TRIDENT)
                ? HumanoidModel.ArmPose.THROW_TRIDENT
                : super.getArmPose(mob, arm);
    }
}