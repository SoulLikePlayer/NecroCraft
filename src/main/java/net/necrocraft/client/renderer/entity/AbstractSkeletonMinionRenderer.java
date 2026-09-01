package net.necrocraft.client.renderer.entity;


import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.monster.skeleton.SkeletonModel;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.Items;
import net.necrocraft.client.model.minion.skeleton.SkeletonMinionModel;
import net.necrocraft.client.renderer.entity.state.SkeletonMinionRenderState;
import net.necrocraft.world.entity.minion.impl.SkeletonMinion;
import net.necrocraft.world.item.ModItems;
import net.neoforged.neoforge.server.command.ModIdArgument;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractSkeletonMinionRenderer<T extends SkeletonMinion, S extends SkeletonMinionRenderState> extends HumanoidMobRenderer<T, S, @NotNull SkeletonMinionModel<S>> {
    public AbstractSkeletonMinionRenderer(EntityRendererProvider.Context context, ModelLayerLocation body, ArmorModelSet<ModelLayerLocation> armorSet) {
        this(context, armorSet, new SkeletonMinionModel<>(context.bakeLayer(body)));
    }

    public AbstractSkeletonMinionRenderer(EntityRendererProvider.Context context, ArmorModelSet<ModelLayerLocation> armorSet, SkeletonMinionModel<S> bodyModel) {
        super(context, bodyModel, 0.5F);
        this.addLayer(new HumanoidArmorLayer(this, ArmorModelSet.bake(armorSet, context.getModelSet(), SkeletonMinionModel::new), context.getEquipmentRenderer()));
    }

    public void extractRenderState(T entity, S state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.isAggressive = entity.isAggressive();
        state.isHoldingBow = entity.getMainHandItem().is(Items.BOW);
        state.isNemesis = entity.getNemesis();
    }

    protected boolean isShaking(S state) {
        return state.isShaking;
    }

    protected HumanoidModel.ArmPose getArmPose(T mob, HumanoidArm arm) {
        return mob.getMainArm() == arm && mob.isAggressive() && mob.getMainHandItem().is(Items.BOW) ? HumanoidModel.ArmPose.BOW_AND_ARROW : super.getArmPose(mob, arm);
    }
}