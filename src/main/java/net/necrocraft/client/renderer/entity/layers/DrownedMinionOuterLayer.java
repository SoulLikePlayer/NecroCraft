package net.necrocraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.Identifier;
import net.necrocraft.client.model.minion.zombie.DrownedMinionModel;
import net.necrocraft.client.renderer.entity.state.ZombieMinionRenderState;
import org.jetbrains.annotations.NotNull;

public class DrownedMinionOuterLayer extends RenderLayer<@NotNull ZombieMinionRenderState, @NotNull DrownedMinionModel> {
    private static final Identifier DROWNED_OUTER_LAYER_LOCATION = Identifier.withDefaultNamespace("textures/entity/zombie/drowned_outer_layer.png");
    private final DrownedMinionModel model;

    public DrownedMinionOuterLayer(RenderLayerParent<@NotNull ZombieMinionRenderState, @NotNull DrownedMinionModel> renderer, EntityModelSet modelSet) {
        super(renderer);
        this.model = new DrownedMinionModel(modelSet.bakeLayer(ModelLayers.DROWNED_OUTER_LAYER));
    }

    public void submit(@NotNull PoseStack poseStack, @NotNull SubmitNodeCollector submitNodeCollector, int lightCoords, ZombieMinionRenderState state, float yRot, float xRot) {
        DrownedMinionModel model = this.model;
        coloredCutoutModelCopyLayerRender(model, DROWNED_OUTER_LAYER_LOCATION, poseStack, submitNodeCollector, lightCoords, state, -1, 1);
    }
}