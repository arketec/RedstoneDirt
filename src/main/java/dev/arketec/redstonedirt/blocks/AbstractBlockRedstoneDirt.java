package dev.arketec.redstonedirt.blocks;

import dev.arketec.redstonedirt.items.ItemInsulatedCover;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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
import net.minecraftforge.common.util.Constants;

import java.util.Random;

public abstract class AbstractBlockRedstoneDirt extends Block implements IRedstonePoweredPlantable {
    public static final IntegerProperty POWER = BlockStateProperties.POWER;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty ENABLED = BlockStateProperties.ENABLED;

    public static final BooleanProperty COVER_TOP = BlockStateProperties.UP;
    public static final BooleanProperty COVER_BOTTOM = BlockStateProperties.DOWN;
    public static final BooleanProperty COVER_NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty COVER_SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty COVER_EAST = BlockStateProperties.EAST;
    public static final BooleanProperty COVER_WEST = BlockStateProperties.WEST;

    protected boolean shouldSignal = true;

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
                        .setValue(COVER_TOP, Boolean.valueOf(false))
                        .setValue(COVER_BOTTOM, Boolean.valueOf(false))
                        .setValue(COVER_NORTH, Boolean.valueOf(false))
                        .setValue(COVER_SOUTH, Boolean.valueOf(false))
                        .setValue(COVER_EAST, Boolean.valueOf(false))
                        .setValue(COVER_WEST, Boolean.valueOf(false))
        );
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity playerEntity, Hand hand, BlockRayTraceResult hit) {
        if (hand.name().equals(Hand.MAIN_HAND.name())) {
            ItemStack held = playerEntity.getItemInHand(hand);
            BlockState newState = state;
            if (held.getItem() instanceof ItemInsulatedCover) {
                newState = addInsulatedCover(world, pos, state, hit.getDirection());
            } else if (held.getItem().equals(Items.AIR) && playerEntity.isCrouching()) {
                newState = removeInsulatedCover(world, pos, state, hit.getDirection());
            }
            world.sendBlockUpdated(pos, newState, newState, Constants.BlockFlags.DEFAULT | Constants.BlockFlags.UPDATE_NEIGHBORS);
        }
        return super.use(state, world, pos, playerEntity, hand, hit);
    };

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public int getDirectSignal(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
        return this.shouldSignal && blockState.getValue(ENABLED) ? getSignal(blockState, blockAccess, pos, side): 0;
    }

    @Override
    public int getSignal(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
        if (isSideCovered(blockState, side)) {
            return 0;
        }
        BlockState state = blockAccess.getBlockState(pos.relative(side.getOpposite()));
        return !this.shouldSignal ? 0: state.getBlock() instanceof RedstoneWireBlock || state.is(this) ? blockState.getValue(POWER) - 1:blockState.getValue(POWER);
    }

    @Override
    public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
        if (side == null || state == null)
            return false;
        return !isSideCovered(state, side);
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(POWER, POWERED, ENABLED, COVER_TOP, COVER_BOTTOM, COVER_EAST, COVER_WEST, COVER_NORTH, COVER_SOUTH);
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

    public BlockState setCoveredState(World world, BlockPos pos, BlockState state, Direction side, boolean isCovered) {
        BlockState newState = state;
        switch (side) {
            case UP:
                newState = newState.setValue(COVER_TOP, Boolean.valueOf(isCovered));
                break;
            case DOWN:
                newState = newState.setValue(COVER_BOTTOM, Boolean.valueOf(isCovered));
                break;
            case NORTH:
                newState = newState.setValue(COVER_NORTH, Boolean.valueOf(isCovered));
                break;
            case SOUTH:
                newState = newState.setValue(COVER_SOUTH, Boolean.valueOf(isCovered));
                break;
            case EAST:
                newState = newState.setValue(COVER_EAST, Boolean.valueOf(isCovered));
                break;
            case WEST:
                newState = newState.setValue(COVER_WEST, Boolean.valueOf(isCovered));
                break;
        }
        setBlockState(world, pos, newState);
        return newState;
    }

    public boolean isSideCovered(BlockState state, Direction side) {
        if (!state.is(this)) {
            return false;
        }
        switch (side) {
            case UP:
                return state.getValue(COVER_TOP);
            case DOWN:
                return state.getValue(COVER_BOTTOM);
            case NORTH:
                return state.getValue(COVER_NORTH);
            case SOUTH:
                return state.getValue(COVER_SOUTH);
            case EAST:
                return state.getValue(COVER_EAST);
            case WEST:
                return state.getValue(COVER_WEST);
        }

        return false;
    }

    public BlockState addInsulatedCover(World world, BlockPos pos, BlockState state, Direction direction) {
        if (!isSideCovered(state, direction)) {
            return setCoveredState(world, pos, state, direction, true);
            // dec player inv
        }
        return state;
    }
    public BlockState removeInsulatedCover(World world, BlockPos pos, BlockState state, Direction direction) {
        if (isSideCovered(state, direction)) {
            return setCoveredState(world, pos, state, direction, false);
            // drop item
        }
        return state;
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
