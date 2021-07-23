package dev.arketec.redstonedirt.blocks;

import dev.arketec.redstonedirt.registration.ModBlocks;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;

import java.util.Random;

public abstract class AbstractBlockRedstoneFarmland extends FarmlandBlock implements IRedstonePoweredPlantable {
    public static final int LIGHT_LEVEL = 5;
    public static final IntegerProperty POWER = BlockStateProperties.POWER;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty ENABLED = BlockStateProperties.ENABLED;

    public AbstractBlockRedstoneFarmland(int defaultPower, boolean defaultPowered, boolean strongPowered) {
        super(Properties
                .of(Material.DIRT)
                .randomTicks()
                .strength(0.6F)
                .sound(SoundType.GRAVEL)
                .isViewBlocking((s, r, p) -> true)
                .isSuffocating((s, r, p) -> true)
                .lightLevel((BlockState state) -> state.getValue(POWERED) ? LIGHT_LEVEL: 0));

        this.registerDefaultState(
                this.getStateDefinition()
                        .any()
                        .setValue(POWER, Integer.valueOf(defaultPower))
                        .setValue(POWERED, Boolean.valueOf(defaultPowered))
                        .setValue(MOISTURE, Integer.valueOf(0))
                        .setValue(ENABLED, strongPowered)
        );
    }

    @Override
    public abstract void tick(BlockState state, ServerWorld world, BlockPos pos, Random random);

    @Override
    public abstract void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random);

    @Override
    public void onPlace(BlockState state, World world, BlockPos pos, BlockState blockState, boolean b) {
        if (!world.isClientSide)
            for (Direction dir: Direction.values()) {
                world.updateNeighborsAt(pos.relative(dir), this);
            }
        super.onPlace(state,world, pos, blockState, b);
    }

    @Override
    public void onRemove(BlockState state, World world, BlockPos pos, BlockState blockState, boolean b) {
        if (!b) {
            for (Direction dir : Direction.values()) {
                world.updateNeighborsAt(pos.relative(dir), this);
            }
        }
        super.onRemove(state, world, pos, blockState, b);
    }

    @Override
    public void fallOn(World world, BlockPos pos, Entity entity, float v) {
        entity.causeFallDamage(v, 1.0F);
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public int getDirectSignal(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
        return blockState.getValue(ENABLED) ? getSignal(blockState, blockAccess, pos, side): 0;
    }

    @Override
    public int getSignal(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
        BlockState state = blockAccess.getBlockState(pos.relative(side.getOpposite()));
        return shouldDecreasePower(state) ? blockState.getValue(POWER) - 1:blockState.getValue(POWER);
    }

    @Override
    public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
        return true;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(POWER, POWERED, MOISTURE, ENABLED);
    }


    @Override
    public boolean canSustainPlant(BlockState state, IBlockReader world, BlockPos pos, Direction facing, IPlantable plantable) {
        PlantType type = plantable.getPlantType(world, pos.offset(facing.getNormal()));
        return type == PlantType.CROP;
    }

    protected boolean isUnderCrops(IBlockReader reader, BlockPos pos) {
        BlockState plant = reader.getBlockState(pos.above());
        BlockState state = reader.getBlockState(pos);
        return plant.getBlock() instanceof IPlantable && state.canSustainPlant(reader, pos, Direction.UP, (IPlantable)plant.getBlock());
    }

    protected static boolean isNearWater(IWorldReader reader, BlockPos pos) {
        for(BlockPos blockpos : BlockPos.betweenClosed(pos.offset(-4, 0, -4), pos.offset(4, 1, 4))) {
            if (reader.getFluidState(blockpos).is(FluidTags.WATER)) {
                return true;
            }
        }

        return net.minecraftforge.common.FarmlandWaterManager.hasBlockWaterTicket(reader, pos);
    }

    public void setBlockState(World world, BlockPos pos, BlockState newState) {
        world.setBlockAndUpdate(pos, newState);
    }

    protected boolean shouldDecreasePower(BlockState blockState) {
        if (blockState.getBlock() instanceof RedstoneWireBlock) {
            return true;
        }
        return blockState.is(ModBlocks.REDSTONE_DIRT.get())
                || blockState.is(ModBlocks.REDSTONE_GRASS.get())
                || blockState.is(ModBlocks.REDSTONE_FARMLAND.get())
                || blockState.is(ModBlocks.REDSTONE_GRASS_PATH.get());

    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void animateTick(BlockState state, World world, BlockPos pos, Random random) {
        if (state.getValue(POWERED)) {
            VoxelShape shape = state.getShape(world, pos);
            if (!shape.isEmpty()) {
                AxisAlignedBB bounds = shape.bounds();
                double x = pos.getX() + bounds.minX + random.nextDouble() * (bounds.maxX - bounds.minX);
                double y = pos.getY() + bounds.minY + random.nextDouble() * (bounds.maxY - bounds.minY);
                double z = pos.getZ() + bounds.minZ + random.nextDouble() * (bounds.maxZ - bounds.minZ);
                world.addParticle(RedstoneParticleData.REDSTONE, x, y, z, 0, 0, 0);
            }
        }
    }
}
