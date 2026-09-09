package net.necrocraft.client.renderer.entity;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.AbstractSkeletonRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ParchedRenderer;
import net.minecraft.client.renderer.entity.SkeletonRenderer;
import net.minecraft.client.renderer.entity.state.SkeletonRenderState;
import net.minecraft.resources.Identifier;
import net.necrocraft.core.NecroCraft;
import net.necrocraft.world.entity.monster.skeleton.Echoing;
import org.jetbrains.annotations.NotNull;

public class EchoingRenderer extends AbstractSkeletonRenderer<@NotNull Echoing, @NotNull SkeletonRenderState> {
    private static final Identifier ECHOING_SKELETON_LOCATION = Identifier.fromNamespaceAndPath(NecroCraft.MODID, "textures/entity/echoing/echoing.png");

    public EchoingRenderer(EntityRendererProvider.Context context) {
        super(context, ModelLayers.SKELETON, ModelLayers.SKELETON_ARMOR);
    }

    public @NotNull Identifier getTextureLocation(SkeletonRenderState state) {
        return ECHOING_SKELETON_LOCATION;
    }

    public SkeletonRenderState createRenderState() {
        return new SkeletonRenderState();
    }
}