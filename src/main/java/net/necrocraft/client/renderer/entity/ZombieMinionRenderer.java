package net.necrocraft.client.renderer.entity;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.necrocraft.client.model.minion.zombie.ZombieMinionModel;
import net.necrocraft.client.renderer.entity.state.ZombieMinionRenderState;
import net.necrocraft.world.entity.minion.impl.ZombieMinion;
import org.jetbrains.annotations.NotNull;

public class ZombieMinionRenderer extends AbstractZombieMinionRenderer<ZombieMinion, ZombieMinionRenderState, ZombieMinionModel<ZombieMinionRenderState>> {
    public ZombieMinionRenderer(EntityRendererProvider.Context context) {
        this(context, ModelLayers.ZOMBIE, ModelLayers.ZOMBIE_ARMOR);
    }

    public ZombieMinionRenderState createRenderState() {
        return new ZombieMinionRenderState();
    }

    public ZombieMinionRenderer(EntityRendererProvider.Context context, ModelLayerLocation body, ArmorModelSet<@NotNull ModelLayerLocation> armorSet) {
        super(context, new ZombieMinionModel<>(context.bakeLayer(body)), ArmorModelSet.bake(armorSet, context.getModelSet(), ZombieMinionModel::new));
    }
}