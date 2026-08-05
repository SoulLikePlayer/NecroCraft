package net.necrocraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.necrocraft.client.model.minion.zombie.DrownedMinionModel;
import net.necrocraft.client.renderer.entity.state.ZombieMinionRenderState;
import net.necrocraft.world.entity.minion.impl.DrownedMinion;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public class DrownedMinionDashRiptideLayer extends RenderLayer<@NotNull ZombieMinionRenderState, DrownedMinionModel> {

    private static final Identifier RIPTIDE_TEXTURE =
            Identifier.withDefaultNamespace("textures/entity/trident/trident_riptide.png");

    private static final int FACE_COUNT = 4;

    private static final float QUAD_HEIGHT = 2.0F;

    private static final float RADIUS = 0.6F;

    private static final float ROT_SPEED = 1.5F * Mth.TWO_PI / 20F;

    public DrownedMinionDashRiptideLayer(
            RenderLayerParent<ZombieMinionRenderState, DrownedMinionModel> parent) {
        super(parent);
    }

    @Override
    public void submit(PoseStack poseStack,
                       SubmitNodeCollector submitNodeCollector,
                       int packedLight,
                       ZombieMinionRenderState state,
                       float yRot,
                       float xRot) {

        if (state.dashPhase != DrownedMinion.DASH_PHASE_DASHING) return;

        float age = state.ageInTicks;
        float baseAngle = age * ROT_SPEED;
        RenderType renderType = RenderTypes.entityCutout(RIPTIDE_TEXTURE);

        float halfWidth = (float) (RADIUS * Math.tan(Math.PI / FACE_COUNT));

        for (int i = 0; i < FACE_COUNT; i++) {
            float faceAngle = baseAngle + (Mth.TWO_PI * i / FACE_COUNT);

            float vOffset = (age / 20F * 0.5F + (float) i / FACE_COUNT) % 1.0F;
            final float uMin = 0.0F;
            final float uMax = 1.0F;
            final float vMin = vOffset;
            final float vMax = vOffset + 0.5F;

            poseStack.pushPose();
            poseStack.translate(0.0, state.boundingBoxHeight * 0.5, 0.0);
            poseStack.mulPose(Axis.YP.rotation(faceAngle));
            poseStack.translate(0.0, 0.0, -RADIUS);

            submitNodeCollector.submitCustomGeometry(poseStack, renderType, (pose, buffer) -> {
                Matrix4f matrix = pose.pose();

                float x0 = -halfWidth;
                float x1 = halfWidth;
                float y0 = -QUAD_HEIGHT * 0.5F;
                float y1 = QUAD_HEIGHT * 0.5F;
                float z = 0.0F;

                buffer.addVertex(matrix, x0, y1, z)
                        .setColor(1f, 1f, 1f, 1f)
                        .setUv(uMin, vMin)
                        .setOverlay(OverlayTexture.NO_OVERLAY)
                        .setLight(packedLight)
                        .setNormal(pose, 0f, 0f, 1f);

                buffer.addVertex(matrix, x1, y1, z)
                        .setColor(1f, 1f, 1f, 1f)
                        .setUv(uMax, vMin)
                        .setOverlay(OverlayTexture.NO_OVERLAY)
                        .setLight(packedLight)
                        .setNormal(pose, 0f, 0f, 1f);

                buffer.addVertex(matrix, x1, y0, z)
                        .setColor(1f, 1f, 1f, 1f)
                        .setUv(uMax, vMax)
                        .setOverlay(OverlayTexture.NO_OVERLAY)
                        .setLight(packedLight)
                        .setNormal(pose, 0f, 0f, 1f);

                buffer.addVertex(matrix, x0, y0, z)
                        .setColor(1f, 1f, 1f, 1f)
                        .setUv(uMin, vMax)
                        .setOverlay(OverlayTexture.NO_OVERLAY)
                        .setLight(packedLight)
                        .setNormal(pose, 0f, 0f, 1f);
            });

            poseStack.popPose();
        }
    }
}