package dev.arketec.redstonedirt.registration;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.GrassColor;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ModColors {

    @OnlyIn(Dist.CLIENT)
    public static void registerBlockColors()
    {
        final BlockColors blockcolors = Minecraft.getInstance().getBlockColors();

        blockcolors.register(
                (state, reader, pos, color) ->
                    reader != null && pos != null ?
                            BiomeColors.getAverageGrassColor(reader, pos) :
                            GrassColor.get(0.5D, 1.0D)
                , ModBlocks.REDSTONE_GRASS.get());
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerItemColors()
    {
        ItemColors itemcolors = Minecraft.getInstance().getItemColors();
        final BlockColors blockcolors = Minecraft.getInstance().getBlockColors();

        itemcolors.register((stack, tintindex) -> {
                    final BlockState state = ((BlockItem) stack.getItem()).getBlock().defaultBlockState();
                    return blockcolors.getColor(state, null, null, tintindex);
                },
                ModBlocks.REDSTONE_GRASS.get());
    }
}
