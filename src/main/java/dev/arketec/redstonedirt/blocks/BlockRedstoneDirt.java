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


public class BlockRedstoneDirt extends AbstractBlockRedstoneDirt {

    public BlockRedstoneDirt() {
        super(0, false,false);
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity playerEntity, Hand hand, BlockRayTraceResult hit) {
        if (hand.name().equals(Hand.MAIN_HAND.name())) {
            ItemStack held = playerEntity.getItemInHand(hand);
            if (held.getItem() instanceof HoeItem && world.isEmptyBlock(pos.above())) {
                held.hurtAndBreak(1, playerEntity, e -> e.broadcastBreakEvent(hand));
                world.setBlockAndUpdate(pos, ModBlocks.REDSTONE_FARMLAND.get().defaultBlockState());
                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.PASS;
    }

    @Override
    public void neighborChanged(BlockState blockState, World world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(blockState, world, pos, block, fromPos, isMoving);
        if (!world.isClientSide()) {
            if (world.hasNeighborSignal(pos) || world.hasNeighborSignal(pos.above())) {
                this.updatePowerStrength(world, pos, blockState);
            } else {
                setBlockState(world, pos, defaultBlockState());
            }
        }
    }

    @Override
    public void onPlace(BlockState state, World world, BlockPos pos, BlockState blockState, boolean b) {
        if (!blockState.is(state.getBlock()) && !world.isClientSide()) {
            this.updatePowerStrength(world, pos, state);
        }
    }

    private void updatePowerStrength(World world, BlockPos pos, BlockState state) {
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
        boolean isBestNeighborThis = false;
        for(Direction direction : Direction.values()) {
            if (world.getSignal(pos.relative(direction), direction) == neighborPower) {
                BlockState blockState = world.getBlockState(pos.relative(direction));
                if (blockState.is(this)) {
                    isBestNeighborThis = true;
                } else if (isBestNeighborThis) {
                    isBestNeighborThis = false;
                    break;
                }
            }
        }
        if (isBestNeighborThis && strength == neighborPower) {
            strength = Math.max(0, strength -1);
        }

        if (state.getValue(POWER) != strength && world.getBlockState(pos) == state) {
            setBlockState(world, pos,
                    state.setValue(POWERED, strength > 0 ? Boolean.valueOf(true): Boolean.valueOf(false))
                            .setValue(POWER, Integer.valueOf(strength)));
        }
    }

    private int getBlockSignal(BlockState state) {
        return state.is(this) ? state.getValue(POWER) : 0;
    }
}
