package dev.arketec.redstonedirt.blocks;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.IPlantable;

public interface IRedstonePoweredPlantable {
    default boolean isSignalSource(BlockState state) {
        return  true;
    }
    default boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
        return true;
    }
    int getDirectSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side);
    int getSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side);
    boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, IPlantable plantable);
    BlockState updatePowerStrength(Level world, BlockPos pos, BlockState state);
}
