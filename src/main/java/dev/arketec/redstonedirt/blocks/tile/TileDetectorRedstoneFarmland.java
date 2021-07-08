package dev.arketec.redstonedirt.blocks.tile;

import dev.arketec.redstonedirt.blocks.BlockDetectorRedstoneFarmland;
import dev.arketec.redstonedirt.registration.ModTileEntityTypes;
import net.minecraft.block.BlockState;
import net.minecraftforge.common.IPlantable;

import java.util.Collections;

import static dev.arketec.redstonedirt.blocks.BlockRedstoneDirt.POWER;
import static dev.arketec.redstonedirt.blocks.BlockRedstoneDirt.POWERED;
import static net.minecraft.state.properties.BlockStateProperties.AGE_7;

public class TileDetectorRedstoneFarmland extends TileDetectorBase {
    public TileDetectorRedstoneFarmland() {
        super(ModTileEntityTypes.DETECTOR_REDSTONE_FARMLAND.get());
    }

    @Override
    protected void tickAction(BlockState blockState) {
        if (blockState.getBlock() instanceof BlockDetectorRedstoneFarmland) {
            BlockDetectorRedstoneFarmland block = ((BlockDetectorRedstoneFarmland) blockState.getBlock());

            if (isUnderCrops(level, worldPosition)) {
                IPlantable crops = (IPlantable)level.getBlockState(worldPosition.above()).getBlock();

                int stage = crops.getPlant(level, worldPosition.above()).getValue(AGE_7);
                if (stage >= Collections.max(AGE_7.getPossibleValues()))
                    block.setBlockState(level, worldPosition,
                            blockState.setValue(POWERED, Boolean.valueOf(true))
                                    .setValue(POWER, Integer.valueOf(15)));
                else
                    block.setBlockState(level, worldPosition,
                            blockState.setValue(POWERED, Boolean.valueOf(true))
                                    .setValue(POWER, Integer.valueOf(stage) + 1));
            } else {
                block.setBlockState(level, worldPosition,
                        blockState.setValue(POWERED, Boolean.valueOf(false))
                                .setValue(POWER, Integer.valueOf(0)));
            }
        }
    }
}
