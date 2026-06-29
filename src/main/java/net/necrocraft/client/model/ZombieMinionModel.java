package net.necrocraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.monster.zombie.AbstractZombieModel;
import net.minecraft.client.renderer.entity.state.ZombieRenderState;
import net.necrocraft.client.model.minion.zombie.AbstractZombieMinionModel;
import net.necrocraft.client.renderer.entity.state.ZombieMinionRenderState;

public class ZombieMinionModel <S extends ZombieMinionRenderState> extends AbstractZombieMinionModel<S> {
    public ZombieMinionModel(ModelPart root) {
        super(root);
    }
}
