package dev.arketec.redstonedirt.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.IPlantable;

public interface IRedstonePoweredPlantable {
    default boolean isSignalSource(BlockState state) {
        return  true;
    }
    default boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
        return true;
    }
    int getDirectSignal(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side);
    int getSignal(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side);
    boolean canSustainPlant(BlockState state, IBlockReader world, BlockPos pos, Direction facing, IPlantable plantable);
}
