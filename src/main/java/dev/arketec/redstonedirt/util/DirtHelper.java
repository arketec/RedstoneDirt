package dev.arketec.redstonedirt.util;

import dev.arketec.redstonedirt.blocks.AbstractBlockRedstoneFarmland;
import dev.arketec.redstonedirt.registration.ModBlocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import static net.minecraft.world.level.block.Block.pushEntitiesUp;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.SNOWY;

public class DirtHelper {
    public static void turnToRedstoneDirt(BlockState state, Level world, BlockPos pos) {
        boolean pushEntitiesUp = state.getBlock() instanceof AbstractBlockRedstoneFarmland;
        world.setBlockAndUpdate(pos, pushEntitiesUp ? pushEntitiesUp(state, ModBlocks.REDSTONE_DIRT.get().defaultBlockState(), world, pos) : ModBlocks.REDSTONE_DIRT.get().defaultBlockState());
    }

    public static void turnToGrass(BlockState state, Level world, BlockPos pos) {
        world.setBlockAndUpdate(pos, Blocks.GRASS_BLOCK.defaultBlockState().setValue(SNOWY, world.getBlockState(pos.above()).is(Blocks.SNOW)));
    }

    public static void turnToRedstoneDirtDetector(BlockState state, Level world, BlockPos pos) {
        boolean pushEntitiesUp = state.getBlock() instanceof AbstractBlockRedstoneFarmland;
        world.setBlockAndUpdate(pos, pushEntitiesUp ? pushEntitiesUp(state, ModBlocks.REDSTONE_DIRT_DETECTOR.get().defaultBlockState(), world, pos): ModBlocks.REDSTONE_DIRT_DETECTOR.get().defaultBlockState());
    }

    public static void turnToPoweredRedstoneDirt(BlockState state, Level world, BlockPos pos) {
        boolean pushEntitiesUp = state.getBlock() instanceof AbstractBlockRedstoneFarmland;
        world.setBlockAndUpdate(pos, pushEntitiesUp ? pushEntitiesUp(state, ModBlocks.REDSTONE_POWERED_DIRT.get().defaultBlockState(), world, pos): ModBlocks.REDSTONE_POWERED_DIRT.get().defaultBlockState());
    }

    public static void turnToRedstoneGrass(BlockState state, Level world, BlockPos pos) {
        world.setBlockAndUpdate(pos, ModBlocks.REDSTONE_GRASS.get().defaultBlockState().setValue(SNOWY, world.getBlockState(pos.above()).is(Blocks.SNOW)));
    }
}
