package net.necrocraft.client.renderer.entity;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.necrocraft.client.renderer.entity.state.SkeletonMinionRenderState;
import net.necrocraft.core.NecroCraft;
import net.necrocraft.world.entity.minion.impl.SkeletonMinion;
import org.jetbrains.annotations.NotNull;

public class SkeletonMinionRenderer extends AbstractSkeletonMinionRenderer<SkeletonMinion, SkeletonMinionRenderState> {
    private static final Identifier SKELETON_LOCATION = Identifier.fromNamespaceAndPath(NecroCraft.MODID, "textures/entity/skeleton/skeleton.png");
    private static final Identifier NEMESIS_SKELETON_LOCATION = Identifier.fromNamespaceAndPath(NecroCraft.MODID, "textures/entity/skeleton/nemesis_skeleton.png");

    public SkeletonMinionRenderer(EntityRendererProvider.Context context) {
        super(context, ModelLayers.SKELETON, ModelLayers.SKELETON_ARMOR);
    }

    public @NotNull Identifier getTextureLocation(SkeletonMinionRenderState state) {
        return state.isNemesis ? NEMESIS_SKELETON_LOCATION : SKELETON_LOCATION;
    }

    public SkeletonMinionRenderState createRenderState() {
        return new SkeletonMinionRenderState();
    }
}