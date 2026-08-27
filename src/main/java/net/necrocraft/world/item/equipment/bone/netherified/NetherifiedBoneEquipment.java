package net.necrocraft.world.item.equipment.bone.netherified;

import net.necrocraft.world.item.equipment.bone.classic.BoneEquipment;

public class NetherifiedBoneEquipment extends BoneEquipment {
    public NetherifiedBoneEquipment(Properties properties) {
        super(properties);
    }

    @Override
    protected float getSoulAddedByTheHurt() {
        return 10.0F;
    }
}
