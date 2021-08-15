package dev.arketec.redstonedirt.blocks.tile;

import dev.arketec.redstonedirt.blocks.IRedstonePoweredPlantable;
import dev.arketec.redstonedirt.configuration.ModConfig;
import dev.arketec.redstonedirt.registration.ModBlocks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.SaplingBlock;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Set;


public abstract class TileDetectorBase extends TileBase {

    private static final int _tickModulus = 10;
    private int _tickCounter = 0;

    public TileDetectorBase(BlockEntityType te, BlockPos pos, BlockState blockState) {
        super(te, pos, blockState);
    }

    public void tickServer() {
        if ((++this._tickCounter % this._tickModulus) != 0) {
            return;
        }
        this._tickCounter = 0;

        if (level != null && !level.isClientSide()) {
            BlockState blockState = level.getBlockState(worldPosition);
            tickAction(blockState);


            // update neighbors
            for(Direction direction : Direction.values()) {
                BlockPos blockpos = worldPosition.relative(direction);

                if (blockState.getBlock() instanceof IRedstonePoweredPlantable)
                    ((IRedstonePoweredPlantable)blockState.getBlock()).updatePowerStrength(level, blockpos, blockState);
            }
        }
    }

    abstract protected void tickAction(BlockState blockState);

    protected boolean isUnderCrops(Level world, BlockPos pos) {
        BlockState plant = world.getBlockState(pos.above());
        BlockState state = world.getBlockState(pos);
        return plant.getBlock() instanceof net.minecraftforge.common.IPlantable && state.canSustainPlant(world, pos, Direction.UP, (net.minecraftforge.common.IPlantable)plant.getBlock());
    }

    protected boolean isUnderSapling(Level world, BlockPos pos) {
        BlockState state = world.getBlockState(pos.above());

        return state.getBlock() instanceof SaplingBlock;
    }

    protected boolean isUnderTree(Level world, BlockPos pos) {
        BlockState blockStateAbove = world.getBlockState(pos.above());
        Set<ResourceLocation> tagsBlockAbove = blockStateAbove.getBlock().getTags();
        final ResourceLocation logsTag = new ResourceLocation("minecraft", "logs");
        return anyMatchTag(logsTag, tagsBlockAbove);
    }

    protected boolean isWithinRange(Level world, BlockPos pos) {
        int range = ModConfig.detectorRange.get();
        return pos.getY() < worldPosition.getY() + range - 1;
    }

    private boolean anyMatchTag(ResourceLocation tag, Set<ResourceLocation> setOfTags) {
        for (ResourceLocation t: setOfTags) {
            if (t.compareTo(tag) == 0)
                return true;
        }
        return false;
    }
}
