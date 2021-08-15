package dev.arketec.redstonedirt.blocks;

import dev.arketec.redstonedirt.registration.ModBlocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.Constants;


public class BlockRedstoneGrass extends AbstractBlockRedstoneGrass {

    public BlockRedstoneGrass() {
        super(0, false,false);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player playerEntity, InteractionHand hand, BlockHitResult hit) {
        if (hand.name().equals(InteractionHand.MAIN_HAND.name())) {
            ItemStack held = playerEntity.getItemInHand(hand);
            if (held.getItem() instanceof HoeItem && world.isEmptyBlock(pos.above())) {
                held.hurtAndBreak(1, playerEntity, e -> e.broadcastBreakEvent(hand));
                world.setBlockAndUpdate(pos, ModBlocks.REDSTONE_FARMLAND.get().defaultBlockState());
                return InteractionResult.SUCCESS;
            }
            if (held.getItem() instanceof ShovelItem && world.isEmptyBlock(pos.above())) {
                held.hurtAndBreak(1, playerEntity, e -> e.broadcastBreakEvent(hand));
                world.setBlockAndUpdate(pos, ModBlocks.REDSTONE_GRASS_PATH.get().defaultBlockState());
                return InteractionResult.SUCCESS;
            }
        }

        return super.use(state, world, pos, playerEntity, hand, hit);
    }

    @Override
    public void neighborChanged(BlockState blockState, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {

        if (!world.isClientSide()) {
            if (world.hasNeighborSignal(pos) || world.hasNeighborSignal(pos.above())) {
                BlockState newState = this.updatePowerStrength(world, pos, blockState);
                world.sendBlockUpdated(pos, newState, newState, Constants.BlockFlags.DEFAULT | Constants.BlockFlags.UPDATE_NEIGHBORS);
            } else {
                setBlockState(world, pos, defaultBlockState());
                world.sendBlockUpdated(pos, defaultBlockState(), defaultBlockState(), Constants.BlockFlags.DEFAULT | Constants.BlockFlags.UPDATE_NEIGHBORS);
            }

        }
        super.neighborChanged(blockState, world, pos, block, fromPos, isMoving);
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState blockState, boolean b) {
        if (!blockState.is(state.getBlock()) && !world.isClientSide()) {
            BlockState newState = this.updatePowerStrength(world, pos, state);
            world.sendBlockUpdated(pos, newState, newState, Constants.BlockFlags.DEFAULT | Constants.BlockFlags.UPDATE_NEIGHBORS);
        }
        super.onPlace(state,world, pos, blockState, b);
    }

    public BlockState updatePowerStrength(Level world, BlockPos pos, BlockState state) {
        int neighborPower = world.getBestNeighborSignal(pos);
        int j = 0;
        if (neighborPower < 15) {
            for(Direction direction : Direction.Plane.HORIZONTAL) {
                BlockPos blockpos = pos.relative(direction);
                BlockState blockstate = world.getBlockState(blockpos);
                j = Math.max(j, getBlockSignal(blockstate));
                BlockPos above = pos.above();
                if (blockstate.isRedstoneConductor(world, blockpos) && !world.getBlockState(above).isRedstoneConductor(world, above)) {
                    j = Math.max(j, getBlockSignal(world.getBlockState(above)));
                } else if (!blockstate.isRedstoneConductor(world, blockpos)) {
                    j = Math.max(j, getBlockSignal(world.getBlockState(blockpos.below())));
                }
            }
        }
        int strength = Math.max(neighborPower, j - 1);

        BlockState newState =state.setValue(POWERED, strength > 0 ? Boolean.valueOf(true): Boolean.valueOf(false))
                .setValue(POWER, Integer.valueOf(strength));
        if (state.getValue(POWER) != strength && world.getBlockState(pos) == state) {
            setBlockState(world, pos,newState );
        }
        return newState;
    }

    private int getBlockSignal(BlockState state) {
        return state.is(this) ? state.getValue(POWER) : 0;
    }
}
