package dev.arketec.redstonedirt.blocks;

import dev.arketec.redstonedirt.registration.ModBlocks;
import dev.arketec.redstonedirt.util.DirtHelper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

import java.util.Random;

public class BlockPoweredRedstoneFarmland extends AbstractBlockRedstoneFarmland {

    public BlockPoweredRedstoneFarmland() {
        super(15, true,true);
    }

    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, Random random) {
        if (!state.canSurvive(world, pos)) {
            DirtHelper.turnToPoweredRedstoneDirt(state, world, pos);
        }
    }

    @Override
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, Random random) {
        int i = state.getValue(MOISTURE);
        if (!isNearWaterHack(world, pos) && !world.isRainingAt(pos.above())) {
            if (i > 0) {
                world.setBlock(pos, state.setValue(MOISTURE, Integer.valueOf(i - 1)), 2);
            } else if (!isUnderCropsHack(world, pos)) {
                DirtHelper.turnToPoweredRedstoneDirt(state, world, pos);
            }
        } else if (i < 7) {
            world.setBlock(pos, state.setValue(MOISTURE, Integer.valueOf(7)), 2);
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext itemUseContext) {
        return !this.defaultBlockState().canSurvive(itemUseContext.getLevel(), itemUseContext.getClickedPos()) ? ModBlocks.REDSTONE_POWERED_DIRT.get().defaultBlockState() : super.getStateForPlacement(itemUseContext);
    }

    @Override
    public void fallOn(Level world, BlockState state, BlockPos pos, Entity entity, float v) {
        if (!world.isClientSide && net.minecraftforge.common.ForgeHooks.onFarmlandTrample(world, pos, ModBlocks.REDSTONE_POWERED_DIRT.get().defaultBlockState(), v, entity)) { // Forge: Move logic to Entity#canTrample
            DirtHelper.turnToPoweredRedstoneDirt(world.getBlockState(pos), world, pos);
        }

        super.fallOn(world, state, pos, entity, v);
    }

    @Override
    public BlockState updatePowerStrength(Level world, BlockPos pos, BlockState state) {
        return state;
    }
}
