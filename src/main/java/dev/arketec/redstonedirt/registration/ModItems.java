package dev.arketec.redstonedirt.registration;


import dev.arketec.redstonedirt.items.ItemInsulatedCover;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;

import java.util.function.Supplier;

public class ModItems {

    public static final void register() {}
    // public final static RegistryObject<Item> INSULATED_COVER = register("insulated_cover", ItemInsulatedCover::new);
    private static <T extends Item> RegistryObject<T> register(String name, Supplier<T> item) {
        return RegistrationManager.ITEMS.register(name, item);
    }
}
