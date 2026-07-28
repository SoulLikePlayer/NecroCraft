package net.necrocraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.monster.skeleton.SkeletonModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.SkeletonRenderState;
import net.minecraft.resources.Identifier;
import net.necrocraft.client.model.minion.skeleton.SkeletonMinionModel;
import net.necrocraft.client.renderer.entity.state.SkeletonMinionRenderState;
import org.jetbrains.annotations.NotNull;

public class SkeletonMinionClothingLayer<S extends SkeletonMinionRenderState, M extends EntityModel<@NotNull S>> extends RenderLayer<@NotNull S, @NotNull M> {
    private final SkeletonMinionModel<S> layerModel;
    private final Identifier clothesLocation;

    public SkeletonMinionClothingLayer(RenderLayerParent<S, M> renderer, EntityModelSet models, ModelLayerLocation layerLocation, Identifier clothesLocation) {
        super(renderer);
        this.clothesLocation = clothesLocation;
        this.layerModel = new SkeletonMinionModel(models.bakeLayer(layerLocation));
    }

    public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, S state, float yRot, float xRot) {
        coloredCutoutModelCopyLayerRender(this.layerModel, this.clothesLocation, poseStack, submitNodeCollector, lightCoords, state, -1, 1);
    }
}
