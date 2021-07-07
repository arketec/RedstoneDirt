package dev.arketec.redstonedirt.blocks;

import dev.arketec.redstonedirt.blocks.BlockRedstoneDirt;
import dev.arketec.redstonedirt.blocks.tile.TileDetectorRedstoneDirt;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SaplingBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MinecartItem;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockDetectorRedstoneDirt extends BlockRedstoneDirt {

    public static final BooleanProperty INVERTED = BlockStateProperties.INVERTED;

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity playerEntity, Hand hand, BlockRayTraceResult hit) {
        if (hand.name() == Hand.MAIN_HAND.name()) {
            ItemStack held = playerEntity.getItemInHand(hand);
            if (held.getItem() instanceof HoeItem) {
                if (state.getValue(INVERTED)) {
                    this.setBlockState(world, pos, this.defaultBlockState().setValue(INVERTED, Boolean.valueOf(false)));
                } else {
                    this.setBlockState(world, pos, this.defaultBlockState().setValue(INVERTED, Boolean.valueOf(true)));
                }
                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.PASS;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TileDetectorRedstoneDirt();
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(POWER, POWERED, INVERTED);
    }
}
