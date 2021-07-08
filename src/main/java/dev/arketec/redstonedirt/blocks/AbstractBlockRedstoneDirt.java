package dev.arketec.redstonedirt.blocks;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.common.ToolType;

import java.util.Random;

public abstract class AbstractBlockRedstoneDirt extends Block {
    public static final IntegerProperty POWER = BlockStateProperties.POWER;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty ENABLED = BlockStateProperties.ENABLED;

    public AbstractBlockRedstoneDirt(int defaultPower, boolean defaultPowered, boolean strongPowered) {
        super(AbstractBlock.Properties.of(Material.DIRT)
                .strength(0.5f)
                .harvestLevel(0)
                .harvestTool(ToolType.SHOVEL)
                .sound(SoundType.GRAVEL)
                .lightLevel((BlockState state) -> state.getValue(POWERED) ? 2: 0)

        );
        this.registerDefaultState(
                this.getStateDefinition()
                        .any()
                        .setValue(POWER, Integer.valueOf(defaultPower))
                        .setValue(POWERED, Boolean.valueOf(defaultPowered))
                        .setValue(ENABLED, Boolean.valueOf(strongPowered))
        );
    }

    @Override
    public abstract ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity playerEntity, Hand hand, BlockRayTraceResult hit);

    @Override
    public boolean isSignalSource(BlockState p_149744_1_) {
        return true;
    }

    @Override
    public int getDirectSignal(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
        return blockState.getValue(ENABLED) ? getSignal(blockState, blockAccess, pos, side): 0;
    }

    @Override
    public int getSignal(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
        return blockState.getValue(POWER);
    }

    @Override
    public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
        return true;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(POWER, POWERED, ENABLED);
    }


    @Override
    public boolean canSustainPlant(BlockState state, IBlockReader world, BlockPos pos, Direction facing, IPlantable plantable) {
        PlantType type = plantable.getPlantType(world, pos.offset(facing.getNormal()));
        return type == PlantType.PLAINS || type == PlantType.BEACH || plantable instanceof StemBlock;
    }

    public  BlockState getFullPoweredState(BlockState state) {
        return state.setValue(POWERED, Boolean.valueOf(true)).setValue(POWER, Integer.valueOf(15));
    }

    public void setBlockState(World world, BlockPos pos, BlockState newState) {
        world.setBlockAndUpdate(pos, newState);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void animateTick(BlockState state, World world, BlockPos pos, Random r) {
        if (state.getValue(POWERED)) {
            VoxelShape shape = state.getShape(world, pos);
            if (!shape.isEmpty()) {
                AxisAlignedBB localBox = shape.bounds();
                double x = pos.getX() + localBox.minX + r.nextDouble() * (localBox.maxX - localBox.minX);
                double y = pos.getY() + localBox.minY + r.nextDouble() * (localBox.maxY - localBox.minY);
                double z = pos.getZ() + localBox.minZ + r.nextDouble() * (localBox.maxZ - localBox.minZ);
                world.addParticle(RedstoneParticleData.REDSTONE, x, y, z, 0, 0, 0);
            }
        }
    }
}