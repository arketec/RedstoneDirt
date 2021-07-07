package dev.arketec.redstonedirt.blocks.tile;

import dev.arketec.redstonedirt.blocks.BlockDetectorRedstoneDirt;
import dev.arketec.redstonedirt.registration.ModTileEntityTypes;
import net.minecraft.block.BlockState;
import net.minecraft.block.SaplingBlock;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import java.util.Set;

import static dev.arketec.redstonedirt.blocks.BlockRedstoneDirt.POWER;
import static dev.arketec.redstonedirt.blocks.BlockRedstoneDirt.POWERED;
import static net.minecraft.state.properties.BlockStateProperties.INVERTED;
import static net.minecraft.state.properties.BlockStateProperties.STAGE;

public class TileDetectorRedstoneDirt extends TileBase implements ITickableTileEntity {

    private static final int _tickModulus = 10;
    private int _tickCounter = 0;
    private boolean _inverted = false;

    public TileDetectorRedstoneDirt() {
        super(ModTileEntityTypes.DETECTOR_REDSTONE_DIRT.get());
    }

    @Override
    public void tick() {
        if ((++this._tickCounter % this._tickModulus) != 0) {
            return;
        }
        this._tickCounter = 0;

        if (!level.isClientSide()) {
            BlockPos posUp = worldPosition.above();

            BlockState blockStateAbove = level.getBlockState(posUp);
            Set<ResourceLocation> tagsBlockAbove = blockStateAbove.getBlock().getTags();
            ResourceLocation logsTag = new ResourceLocation("minecraft", "logs");
            BlockState blockState = level.getBlockState(worldPosition);
            this._inverted = blockState.getValue(INVERTED);
            if (blockState.getBlock() instanceof BlockDetectorRedstoneDirt) {
                BlockDetectorRedstoneDirt block = ((BlockDetectorRedstoneDirt) blockState.getBlock());

                if (blockStateAbove.getBlock() instanceof SaplingBlock) {
                    SaplingBlock saplingBlock = (SaplingBlock) blockStateAbove.getBlock();
                    int stage = saplingBlock.getPlant(level, posUp).getValue(STAGE);

                    block.setBlockState(level, worldPosition,
                            blockState.setValue(POWERED, Boolean.valueOf(true))
                                    .setValue(POWER, Integer.valueOf(stage) + 1));

                } else if (anyMatchTag(logsTag, tagsBlockAbove)) {
                    if (_inverted)
                        block.setBlockState(level, worldPosition,
                                block.defaultBlockState());
                    else
                        block.setBlockState(level, worldPosition,
                                block.getFullPoweredState(blockState));
                } else {
                    if (_inverted)
                        block.setBlockState(level, worldPosition,
                                block.getFullPoweredState(blockState));
                    else
                        block.setBlockState(level, worldPosition,
                                block.defaultBlockState());
                }
            }
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        super.save(tag);
        tag.putBoolean(INVERTED.getName(), _inverted);
        return tag;
    }

    @Override
    public void load(BlockState state, CompoundNBT tag) {
        super.load(state, tag);
        _inverted = tag.getBoolean(INVERTED.getName());
    }

    private boolean anyMatchTag(ResourceLocation tag, Set<ResourceLocation> setOfTags) {
        for (ResourceLocation t: setOfTags) {
            if (t.compareTo(tag) == 0)
                return true;
        }
        return false;
    }
}
