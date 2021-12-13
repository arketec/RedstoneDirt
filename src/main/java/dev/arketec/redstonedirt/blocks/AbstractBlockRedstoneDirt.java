package dev.arketec.redstonedirt.blocks;

import dev.arketec.redstonedirt.registration.ModBlocks;
import dev.arketec.redstonedirt.util.DirtHelper;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.lighting.LayerLightEngine;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;

import java.util.Random;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractBlockRedstoneDirt extends Block implements IRedstonePoweredPlantable {
    public static final int LIGHT_LEVEL = 5;
    public static final IntegerProperty POWER = BlockStateProperties.POWER;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty ENABLED = BlockStateProperties.ENABLED;

    public AbstractBlockRedstoneDirt(int defaultPower, boolean defaultPowered, boolean strongPowered) {
        super(BlockBehaviour.Properties.of(Material.DIRT)
                .randomTicks()
                .strength(0.5f)
                .sound(SoundType.GRAVEL)
                .lightLevel((BlockState state) -> state.getValue(POWERED) ? LIGHT_LEVEL: 0)

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
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player playerEntity, InteractionHand hand, BlockHitResult hit) {
        return super.use(state, world, pos, playerEntity, hand, hit);
    };

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
        builder.add(POWER, POWERED, ENABLED);
    }

    protected static boolean canBeGrass(BlockState state, LevelReader reader, BlockPos pos) {
        BlockPos blockpos = pos.above();
        BlockState blockstate = reader.getBlockState(blockpos);
        if (blockstate.is(Blocks.SNOW) && blockstate.getValue(SnowLayerBlock.LAYERS) == 1) {
            return true;
        } else if (blockstate.getFluidState().getAmount() == 8) {
            return false;
        } else {
            int i = LayerLightEngine.getLightBlockInto(reader, state, pos, blockstate, blockpos, Direction.UP, blockstate.getLightBlock(reader, blockpos));
            return i < reader.getMaxLightLevel();
        }
    }

    protected static boolean canPropagate(BlockState state, LevelReader reader, BlockPos pos) {
        BlockPos blockpos = pos.above();
        return canBeGrass(state, reader, pos) && !reader.getFluidState(blockpos).is(FluidTags.WATER);
    }


    @Override
    public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, IPlantable plantable) {
        PlantType type = plantable.getPlantType(world, pos.offset(facing.getNormal()));
        return type == PlantType.PLAINS || type == PlantType.BEACH || plantable instanceof StemBlock;
    }

    public  BlockState getFullPoweredState(BlockState state) {
        return state.setValue(POWERED, Boolean.valueOf(true)).setValue(POWER, Integer.valueOf(15));
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
    public void animateTick(BlockState state, Level world, BlockPos pos, Random r) {
        if (state.getValue(POWERED)) {
            VoxelShape shape = state.getShape(world, pos);
            if (!shape.isEmpty()) {
                AABB localBox = shape.bounds();
                double x = pos.getX() + localBox.minX + r.nextDouble() * (localBox.maxX - localBox.minX);
                double y = pos.getY() + localBox.minY + r.nextDouble() * (localBox.maxY - localBox.minY);
                double z = pos.getZ() + localBox.minZ + r.nextDouble() * (localBox.maxZ - localBox.minZ);
                world.addParticle(DustParticleOptions.REDSTONE, x, y, z, 0, 0, 0);
            }
        }
    }
}
