package dev.arketec.redstonedirt.blocks;

import dev.arketec.redstonedirt.blocks.tile.TileDetectorBase;
import dev.arketec.redstonedirt.blocks.tile.TileDetectorRedstoneDirt;
import dev.arketec.redstonedirt.registration.ModBlocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockDetectorRedstoneDirt extends AbstractBlockRedstoneDirt implements EntityBlock {

    public BlockDetectorRedstoneDirt() {
        super(0, false,true);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) {
            return null;
        }
        return (level1, blockPos, blockState, t) -> {
            if (t instanceof TileDetectorBase) {
                ((TileDetectorBase)t).tickServer();
            }
        };
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player playerEntity, InteractionHand hand, BlockHitResult hit) {
        if (hand.name().equals(InteractionHand.MAIN_HAND.name())) {
            ItemStack held = playerEntity.getItemInHand(hand);
            if (held.getItem() instanceof HoeItem && world.isEmptyBlock(pos.above())) {
                held.hurtAndBreak(1, playerEntity, e -> e.broadcastBreakEvent(hand));
                world.setBlockAndUpdate(pos, ModBlocks.REDSTONE_FARMLAND_DETECTOR.get().defaultBlockState());
                return InteractionResult.SUCCESS;
            }
        }
        return super.use(state, world, pos, playerEntity, hand, hit);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new TileDetectorRedstoneDirt(blockPos, blockState);
    }

    @Override
    public BlockState updatePowerStrength(Level world, BlockPos pos, BlockState state) {
        return state;
    }

}
