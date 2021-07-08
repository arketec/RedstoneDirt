package dev.arketec.redstonedirt.blocks.tile;

import net.minecraft.block.BlockState;
import net.minecraft.block.SaplingBlock;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import java.util.Set;


public abstract class TileDetectorBase extends TileBase implements ITickableTileEntity {

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
            BlockState blockState = level.getBlockState(worldPosition);
            tickAction(blockState);
        }
    }

    abstract protected void tickAction(BlockState blockState);

    protected boolean isUnderCrops(World world, BlockPos pos) {
        BlockState plant = world.getBlockState(pos.above());
        BlockState state = world.getBlockState(pos);
        return plant.getBlock() instanceof net.minecraftforge.common.IPlantable && state.canSustainPlant(world, pos, Direction.UP, (net.minecraftforge.common.IPlantable)plant.getBlock());
    }

    protected boolean isUnderSapling(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos.above());

        return state.getBlock() instanceof SaplingBlock;
    }

    protected boolean isUnderTree(World world, BlockPos pos) {
        BlockState blockStateAbove = world.getBlockState(pos.above());
        Set<ResourceLocation> tagsBlockAbove = blockStateAbove.getBlock().getTags();
        final ResourceLocation logsTag = new ResourceLocation("minecraft", "logs");
        return anyMatchTag(logsTag, tagsBlockAbove);
    }

    private boolean anyMatchTag(ResourceLocation tag, Set<ResourceLocation> setOfTags) {
        for (ResourceLocation t: setOfTags) {
            if (t.compareTo(tag) == 0)
                return true;
        }
        return false;
    }
}
