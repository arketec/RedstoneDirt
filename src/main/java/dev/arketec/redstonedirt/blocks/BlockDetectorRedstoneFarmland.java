package dev.arketec.redstonedirt.blocks;

import dev.arketec.redstonedirt.blocks.tile.TileDetectorRedstoneFarmland;
import dev.arketec.redstonedirt.registration.ModBlocks;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public class BlockDetectorRedstoneFarmland extends AbstractBlockRedstoneFarmland {

    public BlockDetectorRedstoneFarmland() {
        super(0, false,true);
    }

    @Override
    public void tick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!state.canSurvive(world, pos)) {
            turnToRedstoneDirtDetector(state, world, pos);
        }
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        int i = state.getValue(MOISTURE);
        if (!isNearWater(world, pos) && !world.isRainingAt(pos.above())) {
            if (i > 0) {
                world.setBlock(pos, state.setValue(MOISTURE, Integer.valueOf(i - 1)), 2);
            } else if (!isUnderCrops(world, pos)) {
                turnToRedstoneDirtDetector(state, world, pos);
            }
        } else if (i < 7) {
            world.setBlock(pos, state.setValue(MOISTURE, Integer.valueOf(7)), 2);
        }

    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext itemUseContext) {
        return !this.defaultBlockState().canSurvive(itemUseContext.getLevel(), itemUseContext.getClickedPos()) ? ModBlocks.REDSTONE_DIRT_DETECTOR.get().defaultBlockState() : super.getStateForPlacement(itemUseContext);
    }

    @Override
    public void fallOn(World world, BlockPos pos, Entity entity, float v) {
        if (!world.isClientSide && net.minecraftforge.common.ForgeHooks.onFarmlandTrample(world, pos, ModBlocks.REDSTONE_DIRT_DETECTOR.get().defaultBlockState(), v, entity)) { // Forge: Move logic to Entity#canTrample
            turnToRedstoneDirtDetector(world.getBlockState(pos), world, pos);
        }

        super.fallOn(world, pos, entity, v);
    }
    public static void turnToRedstoneDirtDetector(BlockState state, World world, BlockPos pos) {
        world.setBlockAndUpdate(pos, pushEntitiesUp(state, ModBlocks.REDSTONE_DIRT_DETECTOR.get().defaultBlockState(), world, pos));
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TileDetectorRedstoneFarmland();
    }
}
