package dev.arketec.redstonedirt.registration;

import dev.arketec.redstonedirt.blocks.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.registries.RegistryObject;


import java.util.function.Supplier;

public class ModBlocks {

    public static final void register() {}
    public static final RegistryObject<Block> REDSTONE_DIRT = register("redstone_dirt", () ->
            new BlockRedstoneDirt()
    );

    public static final RegistryObject<Block> REDSTONE_GRASS = register("redstone_grass", () ->
            new BlockRedstoneGrass()
    );

    public static final RegistryObject<Block> REDSTONE_GRASS_PATH = register("redstone_grass_path", () ->
            new BlockRedstoneGrassPath()
    );

    public static final RegistryObject<Block> REDSTONE_FARMLAND = register(
            "redstone_farmland",
            BlockRedstoneFarmland::new
    );

    public static final RegistryObject<Block> REDSTONE_POWERED_DIRT = register("redstone_powered_dirt", () ->
            new BlockPoweredRedstoneDirt()
    );

    public static final RegistryObject<Block> REDSTONE_POWERED_FARMLAND = register("redstone_powered_farmland", () ->
            new BlockPoweredRedstoneFarmland()
    );

    public static final RegistryObject<Block> REDSTONE_DIRT_DETECTOR = register(
            "redstone_dirt_detector",
            BlockDetectorRedstoneDirt::new
    );

    public static final RegistryObject<Block> REDSTONE_FARMLAND_DETECTOR = register(
            "redstone_farmland_detector",
            BlockDetectorRedstoneFarmland::new
    );


    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        return RegistrationManager.BLOCKS.register(name, block);
    }


    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> block) {
        RegistryObject<T> ret = registerBlock(name, block);
        RegistrationManager.ITEMS.register(name, () -> new BlockItem(ret.get(), new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)));
        return  ret;
    }

}
