package net.necrocraft.client.renderer.entity;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.client.renderer.entity.state.ZombieRenderState;
import net.minecraft.resources.Identifier;
import net.necrocraft.client.renderer.entity.state.ZombieMinionRenderState;

public class HuskMinionRenderer extends ZombieMinionRenderer {
    private static final Identifier HUSK_LOCATION = Identifier.withDefaultNamespace("textures/entity/zombie/husk.png");

    public HuskMinionRenderer(EntityRendererProvider.Context context) {
        super(context, ModelLayers.HUSK, ModelLayers.HUSK_ARMOR);
    }

    public Identifier getTextureLocation(ZombieMinionRenderState state) {
        return HUSK_LOCATION;
    }
}
