package net.necrocraft.world.item.book;

import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.necrocraft.client.NecronomiconClientHooks;
import org.jetbrains.annotations.NotNull;

public class NecronomiconItem extends Item {
    private final NecronomiconVolume volume;

    public NecronomiconItem(NecronomiconVolume volume, Properties properties) {
        super(properties);
        this.volume = volume;
    }

    public NecronomiconVolume getVolume() {
        return this.volume;
    }

    @Override
    public @NotNull InteractionResult use(Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        if (level.isClientSide()) {
            NecronomiconClientHooks.openBook(this.volume);
        }
        player.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResult.SUCCESS;
    }
}