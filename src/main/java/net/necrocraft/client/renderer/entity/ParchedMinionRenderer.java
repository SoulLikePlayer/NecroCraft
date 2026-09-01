package net.necrocraft.client.renderer.entity;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.necrocraft.client.renderer.entity.state.SkeletonMinionRenderState;
import net.necrocraft.core.NecroCraft;
import net.necrocraft.world.entity.minion.impl.ParchedMinion;
import org.jetbrains.annotations.NotNull;

public class ParchedMinionRenderer extends AbstractSkeletonMinionRenderer<ParchedMinion, SkeletonMinionRenderState> {
    private static final Identifier PARCHED_SKELETON_LOCATION = Identifier.fromNamespaceAndPath(NecroCraft.MODID,"textures/entity/parched/parched.png");
    private static final Identifier NEMESIS_PARCHED_SKELETON_LOCATION = Identifier.fromNamespaceAndPath(NecroCraft.MODID,"textures/entity/parched/nemesis_parched.png");

    public ParchedMinionRenderer(EntityRendererProvider.Context context) {
        super(context, ModelLayers.PARCHED, ModelLayers.PARCHED_ARMOR);
    }

    public @NotNull Identifier getTextureLocation(SkeletonMinionRenderState state) {
        return state.isNemesis ? NEMESIS_PARCHED_SKELETON_LOCATION : PARCHED_SKELETON_LOCATION;
    }

    public SkeletonMinionRenderState createRenderState() {
        return new SkeletonMinionRenderState();
    }
}