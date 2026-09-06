package net.necrocraft.client.renderer.entity;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.necrocraft.client.model.minion.skeleton.BoggedMinionModel;
import net.necrocraft.client.renderer.entity.layers.SkeletonMinionClothingLayer;
import net.necrocraft.client.renderer.entity.state.SkeletonMinionRenderState;
import net.necrocraft.core.NecroCraft;
import net.necrocraft.world.entity.minion.impl.bogged.BoggedMinion;
import org.jetbrains.annotations.NotNull;

public class BlightedMinionRenderer extends AbstractSkeletonMinionRenderer<BoggedMinion, SkeletonMinionRenderState> {
    private static final Identifier BLIGHTED_SKELETON_LOCATION = Identifier.fromNamespaceAndPath(NecroCraft.MODID,"textures/entity/bogged/blighted/blighted.png");
    private static final Identifier BLIGHTED_OUTER_LAYER_LOCATION = Identifier.fromNamespaceAndPath(NecroCraft.MODID,"textures/entity/bogged/blighted/blighted_overlay.png");

    public BlightedMinionRenderer(EntityRendererProvider.Context context) {
        super(context, ModelLayers.BOGGED_ARMOR, new BoggedMinionModel(context.bakeLayer(ModelLayers.BOGGED)));
        this.addLayer(new SkeletonMinionClothingLayer<>(this, context.getModelSet(), ModelLayers.BOGGED_OUTER_LAYER, BLIGHTED_OUTER_LAYER_LOCATION));
    }

    public @NotNull Identifier getTextureLocation(SkeletonMinionRenderState state) {
        return BLIGHTED_SKELETON_LOCATION;
    }

    public SkeletonMinionRenderState createRenderState() {
        return new SkeletonMinionRenderState();
    }
}