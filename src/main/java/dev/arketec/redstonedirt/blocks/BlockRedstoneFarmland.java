package dev.arketec.redstonedirt.blocks;

import dev.arketec.redstonedirt.registration.ModBlocks;
import dev.arketec.redstonedirt.util.DirtHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

import java.util.Random;

public class BlockRedstoneFarmland extends AbstractBlockRedstoneFarmland {

    public BlockRedstoneFarmland() {
        super(0, false,false);
    }

    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, Random random) {
        if (!state.canSurvive(world, pos)) {
            DirtHelper.turnToRedstoneDirt(state, world, pos);
        }
    }

    @Override
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, Random random) {
        int i = state.getValue(MOISTURE);
        if (!isNearWaterHack(world, pos) && !world.isRainingAt(pos.above())) {
            if (i > 0) {
                world.setBlock(pos, state.setValue(MOISTURE, Integer.valueOf(i - 1)), 2);
            } else if (!isUnderCropsHack(world, pos)) {
                DirtHelper.turnToRedstoneDirt(state, world, pos);
            }
        } else if (i < 7) {
            world.setBlock(pos, state.setValue(MOISTURE, Integer.valueOf(7)), 2);
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext itemUseContext) {
        return !this.defaultBlockState().canSurvive(itemUseContext.getLevel(), itemUseContext.getClickedPos()) ? ModBlocks.REDSTONE_DIRT.get().defaultBlockState() : super.getStateForPlacement(itemUseContext);
    }

    @Override
    public void fallOn(Level world, BlockState state, BlockPos pos, Entity entity, float v) {
        if (!world.isClientSide && net.minecraftforge.common.ForgeHooks.onFarmlandTrample(world, pos, ModBlocks.REDSTONE_DIRT.get().defaultBlockState(), v, entity)) { // Forge: Move logic to Entity#canTrample
            DirtHelper.turnToRedstoneDirt(world.getBlockState(pos), world, pos);
        }

        super.fallOn(world, state, pos, entity, v);
    }
    @Override
    public void neighborChanged(BlockState blockState, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
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
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState blockState, boolean b) {
        if (!blockState.is(state.getBlock()) && !world.isClientSide()) {
            this.updatePowerStrength(world, pos, state);
            super.onPlace(state,world, pos, blockState, b);
        }
    }

    public BlockState updatePowerStrength(Level world, BlockPos pos, BlockState state) {
        int neighborPower = world.getBestNeighborSignal(pos);
        int j = 0;
        if (neighborPower < 15) {
            // check strong powered neighbors of neighbors
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

        BlockState newState = state.setValue(POWERED, strength > 0 ? Boolean.valueOf(true): Boolean.valueOf(false))
                .setValue(POWER, Integer.valueOf(strength));
        if (state.getValue(POWER) != strength && world.getBlockState(pos) == state) {
            setBlockState(world, pos,
                    newState);
        }
        return newState;
    }

    private int getBlockSignal(BlockState state) {
        return state.is(this) ? state.getValue(POWER) : 0;
    }

}
