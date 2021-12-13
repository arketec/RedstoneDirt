package dev.arketec.redstonedirt.blocks.tile;

import dev.arketec.redstonedirt.blocks.BlockDetectorRedstoneDirt;
import dev.arketec.redstonedirt.blocks.BlockDetectorRedstoneFarmland;
import dev.arketec.redstonedirt.registration.ModTileEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.IPlantable;

import java.util.Collections;

import static dev.arketec.redstonedirt.blocks.BlockRedstoneDirt.POWER;
import static dev.arketec.redstonedirt.blocks.BlockRedstoneDirt.POWERED;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.AGE_7;

public class TileDetectorRedstoneFarmland extends TileDetectorBase {
    public TileDetectorRedstoneFarmland(BlockPos pos, BlockState blockState) {
        super(ModTileEntityTypes.DETECTOR_REDSTONE_FARMLAND.get(), pos, blockState);
    }

    @Override
    protected void tickAction(BlockState blockState) {
        if (blockState.getBlock() instanceof BlockDetectorRedstoneFarmland) {
            BlockDetectorRedstoneFarmland block = ((BlockDetectorRedstoneFarmland) blockState.getBlock());
            BlockPos posUp = worldPosition.above();
            handleSaplingDetection(level, worldPosition, block, blockState, level.getBlockState(posUp), posUp);
        }
    }

    protected void handleSaplingDetection(Level world, BlockPos pos, BlockDetectorRedstoneFarmland block, BlockState blockState, BlockState blockStateAbove, BlockPos posUp) {
        if (isUnderCrops(world, pos)) {
            IPlantable crops = (IPlantable)level.getBlockState(pos.above()).getBlock();

            int stage = crops.getPlant(world, pos.above()).getValue(AGE_7);
            if (stage >= Collections.max(AGE_7.getPossibleValues()))
                block.setBlockState(world, worldPosition,
                        blockState.setValue(POWERED, Boolean.valueOf(true))
                                .setValue(POWER, Integer.valueOf(15)));
            else
                block.setBlockState(world, worldPosition,
                        blockState.setValue(POWERED, Boolean.valueOf(true))
                                .setValue(POWER, Integer.valueOf(stage) + 1));
        } else if (isWithinRange(world, pos)) {
            BlockPos posAboveAutoPlanter = posUp.above();
            BlockState blockStateAboveAutoPlanter = world.getBlockState(posAboveAutoPlanter);
            handleSaplingDetection(world, pos.above(), block, blockState, blockStateAboveAutoPlanter, posAboveAutoPlanter);
        } else {
            block.setBlockState(world, worldPosition,
                    blockState.setValue(POWERED, Boolean.valueOf(false))
                            .setValue(POWER, Integer.valueOf(0)));
        }
    }
}
