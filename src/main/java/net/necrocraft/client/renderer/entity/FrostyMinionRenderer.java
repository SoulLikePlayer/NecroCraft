package net.necrocraft.client.renderer.entity;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.necrocraft.client.renderer.entity.layers.SkeletonMinionClothingLayer;
import net.necrocraft.client.renderer.entity.state.SkeletonMinionRenderState;
import net.necrocraft.core.NecroCraft;
import net.necrocraft.world.entity.minion.impl.stray.FrostyMinion;
import org.jetbrains.annotations.NotNull;

public class FrostyMinionRenderer extends AbstractSkeletonMinionRenderer<FrostyMinion, SkeletonMinionRenderState> {
    private static final Identifier FROSTY_SKELETON_LOCATION = Identifier.fromNamespaceAndPath(NecroCraft.MODID,"textures/entity/stray/frosty/frosty.png");
    private static final Identifier STRAY_CLOTHES_LOCATION = Identifier.withDefaultNamespace("textures/entity/skeleton/stray_overlay.png");

    public FrostyMinionRenderer(EntityRendererProvider.Context context) {
        super(context, ModelLayers.STRAY, ModelLayers.STRAY_ARMOR);
        this.addLayer(new SkeletonMinionClothingLayer<>(this, context.getModelSet(), ModelLayers.STRAY_OUTER_LAYER, STRAY_CLOTHES_LOCATION));
    }

    public @NotNull Identifier getTextureLocation(SkeletonMinionRenderState state) {
        return FROSTY_SKELETON_LOCATION;
    }

    public SkeletonMinionRenderState createRenderState() {
        return new SkeletonMinionRenderState();
    }
}