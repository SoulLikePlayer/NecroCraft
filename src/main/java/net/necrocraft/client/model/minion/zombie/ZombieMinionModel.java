package net.necrocraft.client.model.minion.zombie;

import net.minecraft.client.model.geom.ModelPart;
import net.necrocraft.client.renderer.entity.state.ZombieMinionRenderState;

public class ZombieMinionModel <S extends ZombieMinionRenderState> extends AbstractZombieMinionModel<S> {
    public ZombieMinionModel(ModelPart root) {
        super(root);
    }
}
