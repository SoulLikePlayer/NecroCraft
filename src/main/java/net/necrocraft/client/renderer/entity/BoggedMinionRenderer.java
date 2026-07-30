package net.necrocraft.client.renderer.entity;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.SkeletonClothingLayer;
import net.minecraft.resources.Identifier;
import net.necrocraft.client.model.minion.skeleton.BoggedMinionModel;
import net.necrocraft.client.renderer.entity.layers.SkeletonMinionClothingLayer;
import net.necrocraft.client.renderer.entity.state.SkeletonMinionRenderState;
import net.necrocraft.world.entity.minion.BoggedMinion;
import org.jetbrains.annotations.NotNull;

public class BoggedMinionRenderer  extends AbstractSkeletonMinionRenderer<BoggedMinion, SkeletonMinionRenderState> {
    private static final Identifier BOGGED_SKELETON_LOCATION = Identifier.withDefaultNamespace("textures/entity/skeleton/bogged.png");
    private static final Identifier BOGGED_OUTER_LAYER_LOCATION = Identifier.withDefaultNamespace("textures/entity/skeleton/bogged_overlay.png");

    public BoggedMinionRenderer(EntityRendererProvider.Context context) {
        super(context, ModelLayers.BOGGED_ARMOR, new BoggedMinionModel(context.bakeLayer(ModelLayers.BOGGED)));
        this.addLayer(new SkeletonMinionClothingLayer<>(this, context.getModelSet(), ModelLayers.BOGGED_OUTER_LAYER, BOGGED_OUTER_LAYER_LOCATION));
    }

    public @NotNull Identifier getTextureLocation(SkeletonMinionRenderState state) {
        return BOGGED_SKELETON_LOCATION;
    }

    public SkeletonMinionRenderState createRenderState() {
        return new SkeletonMinionRenderState();
    }
}
