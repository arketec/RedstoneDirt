package dev.arketec.redstonedirt.blocks;

import dev.arketec.redstonedirt.registration.ModBlocks;
import net.minecraft.block.*;
import net.minecraft.item.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;


public class BlockPoweredRedstoneDirt extends AbstractBlockRedstoneDirt {

    public BlockPoweredRedstoneDirt() {
        super(15, true, true);
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity playerEntity, Hand hand, BlockRayTraceResult hit) {
        if (hand.name().equals(Hand.MAIN_HAND.name())) {
            ItemStack held = playerEntity.getItemInHand(hand);
            if (held.getItem() instanceof HoeItem && world.isEmptyBlock(pos.above())) {
                held.hurtAndBreak(1, playerEntity, e -> e.broadcastBreakEvent(hand));
                world.setBlockAndUpdate(pos, ModBlocks.REDSTONE_POWERED_FARMLAND.get().defaultBlockState());
                return ActionResultType.SUCCESS;
            }
        }
        return super.use(state, world, pos, playerEntity, hand, hit);
    }

    @Override
    public BlockState updatePowerStrength(World world, BlockPos pos, BlockState state) {
        return state;
    }
}
