package dev.arketec.redstonedirt.blocks;

import dev.arketec.redstonedirt.blocks.tile.TileDetectorBase;
import dev.arketec.redstonedirt.blocks.tile.TileDetectorRedstoneFarmland;
import dev.arketec.redstonedirt.registration.ModBlocks;
import dev.arketec.redstonedirt.util.DirtHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

import java.util.Random;

import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class BlockDetectorRedstoneFarmland extends AbstractBlockRedstoneFarmland implements EntityBlock {

    public BlockDetectorRedstoneFarmland() {
        super(0, false,true);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) {
            return null;
        }
        return (level1, blockPos, blockState, t) -> {
            if (t instanceof TileDetectorBase) {
                ((TileDetectorBase)t).tickServer();
            }
        };
    }

    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, Random random) {
        if (!state.canSurvive(world, pos)) {
            DirtHelper.turnToRedstoneDirtDetector(state, world, pos);
        }
    }

    @Override
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, Random random) {
        int i = state.getValue(MOISTURE);
        if (!isNearWaterHack(world, pos) && !world.isRainingAt(pos.above())) {
            if (i > 0) {
                world.setBlock(pos, state.setValue(MOISTURE, Integer.valueOf(i - 1)), 2);
            } else if (!isUnderCropsHack(world, pos)) {
                DirtHelper.turnToRedstoneDirtDetector(state, world, pos);
            }
        } else if (i < 7) {
            world.setBlock(pos, state.setValue(MOISTURE, Integer.valueOf(7)), 2);
        }

    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext itemUseContext) {
        return !this.defaultBlockState().canSurvive(itemUseContext.getLevel(), itemUseContext.getClickedPos()) ? ModBlocks.REDSTONE_DIRT_DETECTOR.get().defaultBlockState() : super.getStateForPlacement(itemUseContext);
    }

    @Override
    public void fallOn(Level world, BlockState state, BlockPos pos, Entity entity, float v) {
        if (!world.isClientSide && net.minecraftforge.common.ForgeHooks.onFarmlandTrample(world, pos, ModBlocks.REDSTONE_DIRT_DETECTOR.get().defaultBlockState(), v, entity)) { // Forge: Move logic to Entity#canTrample
            DirtHelper.turnToRedstoneDirtDetector(world.getBlockState(pos), world, pos);
        }

        super.fallOn(world, state, pos, entity, v);
    }


    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState blockState) {
        return new TileDetectorRedstoneFarmland(pos, blockState);
    }

    @Override
    public BlockState updatePowerStrength(Level world, BlockPos pos, BlockState state) {
        return state;
    }
}
