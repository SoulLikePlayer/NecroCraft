package net.necrocraft.client.renderer.entity;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.necrocraft.client.renderer.entity.state.ZombieMinionRenderState;
import net.necrocraft.core.NecroCraft;
import org.jetbrains.annotations.NotNull;

public class IgnitedMinionRenderer extends ZombieMinionRenderer {
    private static final Identifier IGNITED_LOCATION = Identifier.fromNamespaceAndPath(NecroCraft.MODID,"textures/entity/husk/ignited/ignited.png");
    private static final Identifier NEMESIS_IGNITED_LOCATION = Identifier.fromNamespaceAndPath(NecroCraft.MODID,"textures/entity/husk/ignited/nemesis_ignited.png");

    public IgnitedMinionRenderer(EntityRendererProvider.Context context) {
        super(context, ModelLayers.HUSK, ModelLayers.HUSK_ARMOR);
    }

    public @NotNull Identifier getTextureLocation(ZombieMinionRenderState state) {
        return state.isNemesis ? NEMESIS_IGNITED_LOCATION : IGNITED_LOCATION;
    }
}