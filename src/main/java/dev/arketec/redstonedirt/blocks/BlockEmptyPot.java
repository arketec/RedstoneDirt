package dev.arketec.redstonedirt.blocks;

import dev.arketec.redstonedirt.registration.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;

public class BlockEmptyPot extends Block {

    public BlockEmptyPot() {
        super(BlockBehaviour.Properties.of(Material.CLAY)
                .strength(0.5f)
                .sound(SoundType.STONE));
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player playerEntity, InteractionHand hand, BlockHitResult hit) {
        if (hand.name().equals(InteractionHand.MAIN_HAND.name())) {
            ItemStack held = playerEntity.getItemInHand(hand);
            if (held.getItem().getRegistryName().getPath().equals("redstone_dirt")) {
                playerEntity.setItemInHand(hand, ItemStack.EMPTY);
                world.setBlockAndUpdate(pos, ModBlocks.REDSTONE_DIRT_POT.get().defaultBlockState());
                return InteractionResult.SUCCESS;
            }
        }
        return super.use(state, world, pos, playerEntity, hand, hit);
    }
}
