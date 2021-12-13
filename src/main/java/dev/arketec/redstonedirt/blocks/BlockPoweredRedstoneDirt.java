package dev.arketec.redstonedirt.blocks;

import dev.arketec.redstonedirt.registration.ModBlocks;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.Level;


import net.minecraft.world.item.HoeItem;
import net.minecraft.world.level.block.state.BlockState;

public class BlockPoweredRedstoneDirt extends AbstractBlockRedstoneDirt {

    public BlockPoweredRedstoneDirt() {
        super(15, true, true);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player playerEntity, InteractionHand hand, BlockHitResult hit) {
        if (hand.name().equals(InteractionHand.MAIN_HAND.name())) {
            ItemStack held = playerEntity.getItemInHand(hand);
            if (held.getItem() instanceof HoeItem && world.isEmptyBlock(pos.above())) {
                held.hurtAndBreak(1, playerEntity, e -> e.broadcastBreakEvent(hand));
                world.setBlockAndUpdate(pos, ModBlocks.REDSTONE_POWERED_FARMLAND.get().defaultBlockState());
                return InteractionResult.SUCCESS;
            }
        }
        return super.use(state, world, pos, playerEntity, hand, hit);
    }

    @Override
    public BlockState updatePowerStrength(Level world, BlockPos pos, BlockState state) {
        return state;
    }
}
