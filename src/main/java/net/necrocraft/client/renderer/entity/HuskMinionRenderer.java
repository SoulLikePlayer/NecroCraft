package net.necrocraft.client.renderer.entity;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.client.renderer.entity.state.ZombieRenderState;
import net.minecraft.resources.Identifier;
import net.necrocraft.client.renderer.entity.state.ZombieMinionRenderState;
import net.necrocraft.core.NecroCraft;
import org.jetbrains.annotations.NotNull;

public class HuskMinionRenderer extends ZombieMinionRenderer {
    private static final Identifier HUSK_LOCATION = Identifier.fromNamespaceAndPath(NecroCraft.MODID,"textures/entity/husk/husk.png");
    private static final Identifier NEMESIS_HUSK_LOCATION = Identifier.fromNamespaceAndPath(NecroCraft.MODID,"textures/entity/husk/nemesis_husk.png");

    public HuskMinionRenderer(EntityRendererProvider.Context context) {
        super(context, ModelLayers.HUSK, ModelLayers.HUSK_ARMOR);
    }

    public @NotNull Identifier getTextureLocation(ZombieMinionRenderState state) {
        return state.isNemesis ? NEMESIS_HUSK_LOCATION : HUSK_LOCATION;
    }
}