package dev.arketec.redstonedirt.blocks;

import dev.arketec.redstonedirt.registration.ModBlocks;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;

import java.util.Random;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractBlockRedstoneFarmland extends FarmBlock implements IRedstonePoweredPlantable {
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
    public abstract void tick(BlockState state, ServerLevel world, BlockPos pos, Random random);

    @Override
    public abstract void randomTick(BlockState state, ServerLevel world, BlockPos pos, Random random);

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState blockState, boolean b) {
        if (!world.isClientSide)
            for (Direction dir: Direction.values()) {
                world.updateNeighborsAt(pos.relative(dir), this);
            }
        super.onPlace(state,world, pos, blockState, b);
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState blockState, boolean b) {
        if (!b) {
            for (Direction dir : Direction.values()) {
                world.updateNeighborsAt(pos.relative(dir), this);
            }
        }
        super.onRemove(state, world, pos, blockState, b);
    }

    @Override
    public void fallOn(Level world, BlockState state, BlockPos pos, Entity entity, float v) {
        entity.causeFallDamage(v, 1.0F, DamageSource.FALL);
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public int getDirectSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
        return blockState.getValue(ENABLED) ? getSignal(blockState, blockAccess, pos, side): 0;
    }

    @Override
    public int getSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
        BlockState state = blockAccess.getBlockState(pos.relative(side.getOpposite()));
        return shouldDecreasePower(state) ? blockState.getValue(POWER) - 1:blockState.getValue(POWER);
    }

    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
        return true;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWER, POWERED, MOISTURE, ENABLED);
    }


    @Override
    public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, IPlantable plantable) {
        PlantType type = plantable.getPlantType(world, pos.offset(facing.getNormal()));
        return type == PlantType.CROP;
    }

    protected boolean isUnderCrops(BlockGetter reader, BlockPos pos) {
        BlockState plant = reader.getBlockState(pos.above());
        BlockState state = reader.getBlockState(pos);
        return plant.getBlock() instanceof IPlantable && state.canSustainPlant(reader, pos, Direction.UP, (IPlantable)plant.getBlock());
    }

    protected static boolean isNearWater(LevelReader reader, BlockPos pos) {
        for(BlockPos blockpos : BlockPos.betweenClosed(pos.offset(-4, 0, -4), pos.offset(4, 1, 4))) {
            if (reader.getFluidState(blockpos).is(FluidTags.WATER)) {
                return true;
            }
        }

        return net.minecraftforge.common.FarmlandWaterManager.hasBlockWaterTicket(reader, pos);
    }

    public void setBlockState(Level world, BlockPos pos, BlockState newState) {
        world.setBlockAndUpdate(pos, newState);
    }

    protected boolean shouldDecreasePower(BlockState blockState) {
        if (blockState.getBlock() instanceof RedStoneWireBlock) {
            return true;
        }
        return blockState.is(ModBlocks.REDSTONE_DIRT.get())
                || blockState.is(ModBlocks.REDSTONE_GRASS.get())
                || blockState.is(ModBlocks.REDSTONE_FARMLAND.get())
                || blockState.is(ModBlocks.REDSTONE_GRASS_PATH.get());

    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, Random random) {
        if (state.getValue(POWERED)) {
            VoxelShape shape = state.getShape(world, pos);
            if (!shape.isEmpty()) {
                AABB bounds = shape.bounds();
                double x = pos.getX() + bounds.minX + random.nextDouble() * (bounds.maxX - bounds.minX);
                double y = pos.getY() + bounds.minY + random.nextDouble() * (bounds.maxY - bounds.minY);
                double z = pos.getZ() + bounds.minZ + random.nextDouble() * (bounds.maxZ - bounds.minZ);
                world.addParticle(DustParticleOptions.REDSTONE, x, y, z, 0, 0, 0);
            }
        }
    }
}
