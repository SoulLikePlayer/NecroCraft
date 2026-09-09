package net.necrocraft.client.renderer.entity;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.SkeletonRenderState;
import net.minecraft.resources.Identifier;
import net.necrocraft.client.renderer.entity.state.SkeletonMinionRenderState;
import net.necrocraft.core.NecroCraft;
import net.necrocraft.world.entity.minion.impl.EchoingMinion;
import org.jetbrains.annotations.NotNull;

public class EchoingMinionRenderer extends AbstractSkeletonMinionRenderer<@NotNull EchoingMinion, @NotNull SkeletonMinionRenderState> {
    private static final Identifier ECHOING_SKELETON_LOCATION = Identifier.fromNamespaceAndPath(NecroCraft.MODID, "textures/entity/echoing/echoing_minion.png");

    public EchoingMinionRenderer(EntityRendererProvider.Context context) {
        super(context, ModelLayers.SKELETON, ModelLayers.SKELETON_ARMOR);
    }

    public @NotNull Identifier getTextureLocation(SkeletonMinionRenderState state) {
        return ECHOING_SKELETON_LOCATION;
    }

    public SkeletonMinionRenderState createRenderState() {
        return new SkeletonMinionRenderState();
    }
}