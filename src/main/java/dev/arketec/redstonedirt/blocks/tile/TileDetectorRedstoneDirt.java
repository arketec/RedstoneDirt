package dev.arketec.redstonedirt.blocks.tile;

import dev.arketec.redstonedirt.blocks.BlockDetectorRedstoneDirt;
import dev.arketec.redstonedirt.registration.ModTileEntityTypes;
import net.minecraft.block.BlockState;
import net.minecraft.block.SaplingBlock;
import net.minecraft.util.math.BlockPos;

import static dev.arketec.redstonedirt.blocks.BlockRedstoneDirt.POWER;
import static dev.arketec.redstonedirt.blocks.BlockRedstoneDirt.POWERED;
import static net.minecraft.state.properties.BlockStateProperties.STAGE;


public class TileDetectorRedstoneDirt extends TileDetectorBase {
    public TileDetectorRedstoneDirt() {
        super(ModTileEntityTypes.DETECTOR_REDSTONE_DIRT.get());
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
