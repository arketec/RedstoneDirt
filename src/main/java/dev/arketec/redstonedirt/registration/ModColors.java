package dev.arketec.redstonedirt.registration;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.BlockItem;
import net.minecraft.world.GrassColors;
import net.minecraft.world.biome.BiomeColors;
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
                            GrassColors.get(0.5D, 1.0D)
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
