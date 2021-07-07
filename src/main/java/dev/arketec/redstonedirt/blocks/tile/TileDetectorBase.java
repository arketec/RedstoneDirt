package dev.arketec.redstonedirt.blocks.tile;

import dev.arketec.redstonedirt.blocks.BlockDetectorRedstoneDirt;
import dev.arketec.redstonedirt.blocks.BlockDetectorRedstoneFarmland;
import dev.arketec.redstonedirt.registration.ModTileEntityTypes;
import net.minecraft.block.BlockState;
import net.minecraft.block.SaplingBlock;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.RegistryObject;

import java.util.Collections;
import java.util.Set;

import static dev.arketec.redstonedirt.blocks.BlockRedstoneDirt.POWER;
import static dev.arketec.redstonedirt.blocks.BlockRedstoneDirt.POWERED;
import static net.minecraft.state.properties.BlockStateProperties.STAGE;
import static net.minecraft.state.properties.BlockStateProperties.AGE_7;

public class TileDetectorBase extends TileBase implements ITickableTileEntity {

    private static final int _tickModulus = 10;
    private int _tickCounter = 0;

    public TileDetectorBase(TileEntityType te) {
        super(te);
    }

    @Override
    public void tick() {
        if ((++this._tickCounter % this._tickModulus) != 0) {
            return;
        }
        this._tickCounter = 0;

        if (level != null && !level.isClientSide()) {
            BlockPos posUp = worldPosition.above();

            BlockState blockStateAbove = level.getBlockState(posUp);
            Set<ResourceLocation> tagsBlockAbove = blockStateAbove.getBlock().getTags();
            ResourceLocation logsTag = new ResourceLocation("minecraft", "logs");
            BlockState blockState = level.getBlockState(worldPosition);
            if (blockState.getBlock() instanceof BlockDetectorRedstoneDirt) {
                BlockDetectorRedstoneDirt block = ((BlockDetectorRedstoneDirt) blockState.getBlock());

                if (blockStateAbove.getBlock() instanceof SaplingBlock) {
                    SaplingBlock saplingBlock = (SaplingBlock) blockStateAbove.getBlock();
                    int stage = saplingBlock.getPlant(level, posUp).getValue(STAGE);

                    block.setBlockState(level, worldPosition,
                            blockState.setValue(POWERED, Boolean.valueOf(true))
                                    .setValue(POWER, Integer.valueOf(stage) + 1));

                } else if (anyMatchTag(logsTag, tagsBlockAbove)) {
                    block.setBlockState(level, worldPosition,
                            block.getFullPoweredState(blockState));

                } else {
                    block.setBlockState(level, worldPosition,
                            block.defaultBlockState());

                }
            }
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

    private boolean anyMatchTag(ResourceLocation tag, Set<ResourceLocation> setOfTags) {
        for (ResourceLocation t: setOfTags) {
            if (t.compareTo(tag) == 0)
                return true;
        }
        return false;
    }

    private boolean isUnderCrops(World world, BlockPos pos) {
        BlockState plant = world.getBlockState(pos.above());
        BlockState state = world.getBlockState(pos);
        return plant.getBlock() instanceof net.minecraftforge.common.IPlantable && state.canSustainPlant(world, pos, Direction.UP, (net.minecraftforge.common.IPlantable)plant.getBlock());
    }
}
