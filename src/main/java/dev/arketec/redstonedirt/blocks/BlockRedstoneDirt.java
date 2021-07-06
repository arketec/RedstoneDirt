package dev.arketec.redstonedirt.blocks;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.common.ToolType;


public class BlockRedstoneDirt extends Block {
    public static final IntegerProperty POWER = BlockStateProperties.POWER;

    public BlockRedstoneDirt() {
        super(AbstractBlock.Properties.of(Material.DIRT)
                .strength(0.5f)
                .randomTicks()
                .harvestLevel(0)
                .harvestTool(ToolType.SHOVEL)
                .sound(SoundType.GRAVEL)
                .lightLevel((BlockState state) -> 2)

        );
        this.registerDefaultState(this.stateDefinition
                .any()
                .setValue(POWER, Integer.valueOf(15))
        );
    }

    @Override
    public boolean isSignalSource(BlockState p_149744_1_) {
        return true;
    }

    @Override
    public int getSignal(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
        return getDirectSignal(blockState, blockAccess, pos, side);
    }

    @Override
    public int getDirectSignal(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
        return blockState.getValue(POWER);
    }

    @Override
    public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
        return true;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(POWER);
    }

    @Override
    public boolean canSustainPlant(BlockState state, IBlockReader world, BlockPos pos, Direction facing, IPlantable plantable) {
        PlantType type = plantable.getPlantType(world, pos.offset(facing.getNormal()));
        return type == PlantType.PLAINS || type == PlantType.BEACH || plantable instanceof StemBlock;
    }
}
