package net.necrocraft.world.item.bonus.impl.synced;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.necrocraft.core.NecroCraft;
import net.necrocraft.world.entity.minion.AbstractMinion;
import net.necrocraft.world.item.bonus.AbstractBonusItem;
import net.necrocraft.world.item.bonus.BonusTrigger;
import net.necrocraft.world.item.bonus.BonusType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class MummyWrappingBonusItem extends AbstractBonusItem {

    private static final float HEAL_AMOUNT_ON_HIT = 1.0F;

    public MummyWrappingBonusItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull BonusType getBonusTypes() {
        return BonusType.PASSIVE;
    }

    @Override
    public void applyEffectes(@NotNull AbstractMinion minion) {
        minion.addEffect(new MobEffectInstance(MobEffects.REGENERATION, MobEffectInstance.INFINITE_DURATION, 0, true, true));
        NecroCraft.LOGGER.info("Regeneration");
    }

    @Override
    public void applySyncedEffect(@NotNull AbstractMinion minion) {
        minion.heal(HEAL_AMOUNT_ON_HIT);
        spawnLifeDrainEffect((ServerLevel) minion.level(), Objects.requireNonNull(minion.getTarget()), minion);
    }

    public @NotNull BonusTrigger getBonusTrigger(){
        return BonusTrigger.ON_DAMAGE;
    }

    private void spawnLifeDrainEffect(@NotNull ServerLevel level, @NotNull Entity target, AbstractMinion minion) {
        Vec3 from = target.position().add(0.0D, target.getBbHeight() * 0.5D, 0.0D);
        Vec3 to = minion.position().add(0.0D, minion.getBbHeight() * 0.5D, 0.0D);
        Vec3 delta = to.subtract(from);

        int steps = 10;
        for (int i = 0; i <= steps; i++) {
            double t = (double) i / steps;
            Vec3 point = from.add(delta.scale(t));
            level.sendParticles(ParticleTypes.SOUL, point.x, point.y, point.z,
                    1, 0.02D, 0.02D, 0.02D, 0.0D);
        }
    }
}
