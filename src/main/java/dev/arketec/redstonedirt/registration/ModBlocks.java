package dev.arketec.redstonedirt.registration;

import dev.arketec.redstonedirt.blocks.BlockDetectorRedstoneDirt;
import dev.arketec.redstonedirt.blocks.BlockDetectorRedstoneFarmland;
import dev.arketec.redstonedirt.blocks.BlockRedstoneDirt;
import dev.arketec.redstonedirt.blocks.BlockRedstoneFarmland;
import dev.arketec.redstonedirt.blocks.tile.TileDetectorRedstoneDirt;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {

    public static final void register() {}
    public static final RegistryObject<Block> REDSTONE_DIRT = register("redstone_dirt", () ->
            new BlockRedstoneDirt()
    );

    public static final RegistryObject<Block> REDSTONE_FARMLAND = register(
            "redstone_farmland",
            BlockRedstoneFarmland::new
    );

    public static final RegistryObject<Block> REDSTONE_DIRT_DETECTOR = register(
            "redstone_dirt_detector",
            BlockDetectorRedstoneDirt::new
    );

    public static final RegistryObject<Block> REDSTONE_FARMLAND_DETECTOR = register(
            "redstone_farmland_detector",
            BlockDetectorRedstoneFarmland::new
    );

//    public static final RegistryObject<Block> REDSTONE_DIRT_FLIP_FLOP = register(
//            "redstone_dirt_flip_flop",
//            BlockRedstoneDirtFlipFlop::new
//    );

//    public static final RegistryObject<Block> REDSTONE_Farmland_FLIP_FLOP = register(
//            "redstone_farmland_flip_flop",
//            BlockRedstoneFarmlandFlipFlop::new
//    );

    // rest of basic logic gates

    // ender redstone dirt (entangled block states)

    // redstone sand?

    // magnetic sand (iron shavings)???

    // magnetic oil (ferrous fluid)?????

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        return RegistrationManager.BLOCKS.register(name, block);
    }


    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> block) {
        RegistryObject<T> ret = registerBlock(name, block);
        RegistrationManager.ITEMS.register(name, () -> new BlockItem(ret.get(), new Item.Properties().tab(ItemGroup.TAB_BUILDING_BLOCKS)));
        return  ret;
    }

}
