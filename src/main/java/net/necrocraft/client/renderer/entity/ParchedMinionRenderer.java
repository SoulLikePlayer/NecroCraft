package net.necrocraft.client.renderer.entity;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.necrocraft.client.renderer.entity.state.SkeletonMinionRenderState;
import net.necrocraft.world.entity.minion.ParchedMinion;
import org.jetbrains.annotations.NotNull;

public class ParchedMinionRenderer extends AbstractSkeletonMinionRenderer<ParchedMinion, SkeletonMinionRenderState> {
    private static final Identifier PARCHED_SKELETON_LOCATION = Identifier.withDefaultNamespace("textures/entity/skeleton/parched.png");

    public ParchedMinionRenderer(EntityRendererProvider.Context context) {
        super(context, ModelLayers.PARCHED, ModelLayers.PARCHED_ARMOR);
    }

    public @NotNull Identifier getTextureLocation(SkeletonMinionRenderState state) {
        return PARCHED_SKELETON_LOCATION;
    }

    public SkeletonMinionRenderState createRenderState() {
        return new SkeletonMinionRenderState();
    }
}