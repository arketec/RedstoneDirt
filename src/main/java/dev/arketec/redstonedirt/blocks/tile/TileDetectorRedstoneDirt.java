package dev.arketec.redstonedirt.blocks.tile;

import dev.arketec.redstonedirt.blocks.BlockDetectorRedstoneDirt;
import dev.arketec.redstonedirt.registration.ModTileEntityTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.core.BlockPos;

import static dev.arketec.redstonedirt.blocks.BlockRedstoneDirt.POWER;
import static dev.arketec.redstonedirt.blocks.BlockRedstoneDirt.POWERED;
import static net.minecraft.world.level.block.SaplingBlock.STAGE;


public class TileDetectorRedstoneDirt extends TileDetectorBase {
    public TileDetectorRedstoneDirt(BlockPos pos, BlockState blockState) {
        super(ModTileEntityTypes.DETECTOR_REDSTONE_DIRT.get(), pos, blockState);
    }

    @Override
    protected void tickAction(BlockState blockState) {
        if (blockState.getBlock() instanceof BlockDetectorRedstoneDirt) {
            BlockPos posUp = worldPosition.above();
            BlockState blockStateAbove = level.getBlockState(posUp);
            BlockDetectorRedstoneDirt block = ((BlockDetectorRedstoneDirt) blockState.getBlock());

            if (isUnderSapling(level, worldPosition)) {
                SaplingBlock saplingBlock = (SaplingBlock) blockStateAbove.getBlock();
                int stage = saplingBlock.getPlant(level, posUp).getValue(STAGE);

                block.setBlockState(level, worldPosition,
                        blockState.setValue(POWERED, Boolean.valueOf(true))
                                .setValue(POWER, Integer.valueOf(stage) + 1));

            } else if (isUnderTree(level, worldPosition)) {
                block.setBlockState(level, worldPosition,
                        block.getFullPoweredState(blockState));

            } else {
                block.setBlockState(level, worldPosition,
                        block.defaultBlockState());

            }
        }
    }
}
