package dev.arketec.redstonedirt.util;

import dev.arketec.redstonedirt.blocks.AbstractBlockRedstoneFarmland;
import dev.arketec.redstonedirt.registration.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static net.minecraft.block.Block.pushEntitiesUp;
import static net.minecraft.state.properties.BlockStateProperties.SNOWY;

public class DirtHelper {
    public static void turnToRedstoneDirt(BlockState state, World world, BlockPos pos) {
        boolean pushEntitiesUp = state.getBlock() instanceof AbstractBlockRedstoneFarmland;
        world.setBlockAndUpdate(pos, pushEntitiesUp ? pushEntitiesUp(state, ModBlocks.REDSTONE_DIRT.get().defaultBlockState(), world, pos) : ModBlocks.REDSTONE_DIRT.get().defaultBlockState());
    }

    public static void turnToGrass(BlockState state, World world, BlockPos pos) {
        world.setBlockAndUpdate(pos, Blocks.GRASS_BLOCK.defaultBlockState().setValue(SNOWY, world.getBlockState(pos.above()).is(Blocks.SNOW)));
    }

    public static void turnToRedstoneDirtDetector(BlockState state, World world, BlockPos pos) {
        boolean pushEntitiesUp = state.getBlock() instanceof AbstractBlockRedstoneFarmland;
        world.setBlockAndUpdate(pos, pushEntitiesUp ? pushEntitiesUp(state, ModBlocks.REDSTONE_DIRT_DETECTOR.get().defaultBlockState(), world, pos): ModBlocks.REDSTONE_DIRT_DETECTOR.get().defaultBlockState());
    }

    public static void turnToPoweredRedstoneDirt(BlockState state, World world, BlockPos pos) {
        boolean pushEntitiesUp = state.getBlock() instanceof AbstractBlockRedstoneFarmland;
        world.setBlockAndUpdate(pos, pushEntitiesUp ? pushEntitiesUp(state, ModBlocks.REDSTONE_POWERED_DIRT.get().defaultBlockState(), world, pos): ModBlocks.REDSTONE_POWERED_DIRT.get().defaultBlockState());
    }

    public static void turnToRedstoneGrass(BlockState state, World world, BlockPos pos) {
        world.setBlockAndUpdate(pos, ModBlocks.REDSTONE_GRASS.get().defaultBlockState().setValue(SNOWY, world.getBlockState(pos.above()).is(Blocks.SNOW)));
    }
}
