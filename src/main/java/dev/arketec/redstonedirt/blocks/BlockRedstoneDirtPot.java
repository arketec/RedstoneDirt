package dev.arketec.redstonedirt.blocks;

import dev.arketec.redstonedirt.registration.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.ticks.ScheduledTick;

public class BlockRedstoneDirtPot extends AbstractBlockRedstoneDirt {

    public static final BooleanProperty ATTACHED = BlockStateProperties.ATTACHED;
    public BlockRedstoneDirtPot() {
        super(0, false,false);
        registerDefaultState(this.getStateDefinition().any()
                .setValue(ATTACHED, false));
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player playerEntity, InteractionHand hand, BlockHitResult hit) {
        if (hand.name().equals(InteractionHand.MAIN_HAND.name())) {
            ItemStack held = playerEntity.getItemInHand(hand);
            if (!state.getValue(ATTACHED) && held.getItem().getRegistryName().getPath().equals("redstone_dirt")) {
                playerEntity.setItemInHand(hand, ItemStack.EMPTY);
                setBlockState(world, pos, state.setValue(ATTACHED, true));
                return InteractionResult.SUCCESS;
            }  else if (held.isEmpty() && playerEntity.isCrouching() && state.getValue(ATTACHED)) {
                setBlockState(world, pos, defaultBlockState());
                playerEntity.addItem(new ItemStack(ModBlocks.REDSTONE_DIRT.get().asItem()));
                return InteractionResult.SUCCESS;
            }
        }
        return super.use(state, world, pos, playerEntity, hand, hit);
    }

    @Override
    public void neighborChanged(BlockState blockState, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(blockState, world, pos, block, fromPos, isMoving);
        if (!world.isClientSide()) {
            if (world.hasNeighborSignal(pos) || world.hasNeighborSignal(pos.above())) {
                BlockState newState = this.updatePowerStrength(world, pos, blockState);
                world.sendBlockUpdated(pos, newState, newState, UPDATE_ALL | UPDATE_NEIGHBORS);
            } else {
                setBlockState(world, pos, defaultBlockState());
                world.sendBlockUpdated(pos, defaultBlockState(), defaultBlockState(), UPDATE_ALL | UPDATE_NEIGHBORS);
                world.getBlockTicks().schedule(new ScheduledTick(this, pos, 2, 2));
            }

        }
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState blockState, boolean b) {
        if (!blockState.is(state.getBlock()) && !world.isClientSide()) {
            BlockState newState = this.updatePowerStrength(world, pos, state);
            world.sendBlockUpdated(pos, newState, newState, UPDATE_ALL | UPDATE_NEIGHBORS);
            super.onPlace(state,world,pos,blockState,b);
        }
    }

    public BlockState updatePowerStrength(Level world, BlockPos pos, BlockState state) {
        int neighborPower = world.getBestNeighborSignal(pos);
        int j = 0;
        if (neighborPower < 15) {
            for(Direction direction : Direction.Plane.HORIZONTAL) {
                BlockPos blockpos = pos.relative(direction);
                BlockState blockstate = world.getBlockState(blockpos);
                j = Math.max(j, getBlockSignal(blockstate));
                BlockPos above = pos.above();
                if (blockstate.isRedstoneConductor(world, blockpos) && !world.getBlockState(above).isRedstoneConductor(world, above)) {
                    j = Math.max(j, getBlockSignal(world.getBlockState(above)));
                } else if (!blockstate.isRedstoneConductor(world, blockpos)) {
                    j = Math.max(j, getBlockSignal(world.getBlockState(blockpos.below())));
                }
            }
        }
        int strength = Math.max(neighborPower, j - 1);

        BlockState newState =state.setValue(POWERED, strength > 0 ? Boolean.valueOf(true): Boolean.valueOf(false))
                .setValue(POWER, Integer.valueOf(strength));
        if (state.getValue(POWER) != strength && world.getBlockState(pos) == state) {
            setBlockState(world, pos,newState );
        }
        return newState;
    }

    private int getBlockSignal(BlockState state) {
        return state.is(this) ? state.getValue(POWER) : 0;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWER, POWERED, ENABLED, ATTACHED);
    }
}
