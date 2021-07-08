package dev.arketec.redstonedirt.blocks;

import dev.arketec.redstonedirt.registration.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public class BlockRedstoneFarmland extends AbstractBlockRedstoneFarmland {

    public BlockRedstoneFarmland() {
        super(false);
    }

    @Override
    public void tick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!state.canSurvive(world, pos)) {
            turnToRedstoneDirt(state, world, pos);
        }
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        int i = state.getValue(MOISTURE);
        if (!isNearWater(world, pos) && !world.isRainingAt(pos.above())) {
            if (i > 0) {
                world.setBlock(pos, state.setValue(MOISTURE, Integer.valueOf(i - 1)), 2);
            } else if (!isUnderCrops(world, pos)) {
                turnToRedstoneDirt(state, world, pos);
            }
        } else if (i < 7) {
            world.setBlock(pos, state.setValue(MOISTURE, Integer.valueOf(7)), 2);
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext itemUseContext) {
        return !this.defaultBlockState().canSurvive(itemUseContext.getLevel(), itemUseContext.getClickedPos()) ? ModBlocks.REDSTONE_DIRT.get().defaultBlockState() : super.getStateForPlacement(itemUseContext);
    }

    @Override
    public void fallOn(World world, BlockPos pos, Entity entity, float v) {
        if (!world.isClientSide && net.minecraftforge.common.ForgeHooks.onFarmlandTrample(world, pos, ModBlocks.REDSTONE_DIRT.get().defaultBlockState(), v, entity)) { // Forge: Move logic to Entity#canTrample
            turnToRedstoneDirt(world.getBlockState(pos), world, pos);
        }

        super.fallOn(world, pos, entity, v);
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
                    state.setValue(POWERED, neighborPower > 1 ? Boolean.valueOf(true): Boolean.valueOf(false))
                            .setValue(POWER, Integer.valueOf(strength)));
        }
    }

    private int getBlockSignal(BlockState state) {
        return state.is(this) ? state.getValue(POWER) : 0;
    }

    public static void turnToRedstoneDirt(BlockState state, World world, BlockPos pos) {
        world.setBlockAndUpdate(pos, pushEntitiesUp(state, ModBlocks.REDSTONE_DIRT.get().defaultBlockState(), world, pos));
    }


}
