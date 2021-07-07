package dev.arketec.redstonedirt.registration;

import dev.arketec.redstonedirt.blocks.tile.TileDetectorRedstoneDirt;
import dev.arketec.redstonedirt.blocks.tile.TileDetectorRedstoneFarmland;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;

import java.util.function.Supplier;

public class ModTileEntityTypes {

    public static final RegistryObject<TileEntityType<TileDetectorRedstoneDirt>> DETECTOR_REDSTONE_DIRT = register("redstone_dirt_detector", TileDetectorRedstoneDirt::new, ModBlocks.REDSTONE_DIRT_DETECTOR);
    public static final RegistryObject<TileEntityType<TileDetectorRedstoneFarmland>> DETECTOR_REDSTONE_FARMLAND = register("redstone_farmland_detector", TileDetectorRedstoneFarmland::new, ModBlocks.REDSTONE_FARMLAND_DETECTOR);
    static void register() {}

    private static <T extends TileEntity> RegistryObject<TileEntityType<T>> register(String name, Supplier<T> factory, RegistryObject<? extends Block> block) {
        return RegistrationManager.TILE_ENTITIES.register(name, () -> TileEntityType.Builder.of(factory, block.get()).build(null));
    }
}
