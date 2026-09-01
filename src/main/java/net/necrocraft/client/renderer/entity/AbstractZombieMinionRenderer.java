package net.necrocraft.client.renderer.entity;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.SwingAnimationType;
import net.minecraft.world.item.component.SwingAnimation;
import net.necrocraft.client.model.minion.zombie.ZombieMinionModel;
import net.necrocraft.client.renderer.entity.state.ZombieMinionRenderState;
import net.necrocraft.core.NecroCraft;
import net.necrocraft.world.entity.minion.impl.ZombieMinion;
import net.necrocraft.world.item.ModItems;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractZombieMinionRenderer<T extends ZombieMinion, S extends ZombieMinionRenderState, M extends ZombieMinionModel<S>> extends HumanoidMobRenderer<T, S, M> {
    private static final Identifier ZOMBIE_LOCATION = Identifier.fromNamespaceAndPath(NecroCraft.MODID,"textures/entity/zombie/zombie.png");
    private static final Identifier NEMESIS_ZOMBIE_LOCATION = Identifier.fromNamespaceAndPath(NecroCraft.MODID,"textures/entity/zombie/nemesis_zombie.png");

    protected AbstractZombieMinionRenderer(EntityRendererProvider.Context context, M model, ArmorModelSet<M> armorSet) {
        super(context, model, 0.5F);
        this.addLayer(new HumanoidArmorLayer(this, armorSet, context.getEquipmentRenderer()));
    }

    public @NotNull Identifier getTextureLocation(S state) {
        return state.isNemesis ? NEMESIS_ZOMBIE_LOCATION : ZOMBIE_LOCATION;
    }

    public void extractRenderState(T entity, S state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.isAggressive = entity.isAggressive();
        state.isNemesis = entity.getNemesis();
    }

    protected boolean isShaking(S state) {
        return super.isShaking(state) || state.isConverting;
    }

    protected HumanoidModel.ArmPose getArmPose(T mob, HumanoidArm arm) {
        SwingAnimation otherAnim = (SwingAnimation)mob.getItemHeldByArm(arm.getOpposite()).get(DataComponents.SWING_ANIMATION);
        return otherAnim != null && otherAnim.type() == SwingAnimationType.STAB ? HumanoidModel.ArmPose.SPEAR : super.getArmPose(mob, arm);
    }
}