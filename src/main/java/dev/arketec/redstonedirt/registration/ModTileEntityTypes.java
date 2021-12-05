package dev.arketec.redstonedirt.registration;

import dev.arketec.redstonedirt.blocks.tile.TileDetectorRedstoneDirt;
import dev.arketec.redstonedirt.blocks.tile.TileDetectorRedstoneFarmland;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModTileEntityTypes {

    public static final RegistryObject<BlockEntityType<TileDetectorRedstoneDirt>> DETECTOR_REDSTONE_DIRT = register("redstone_dirt_detector", TileDetectorRedstoneDirt::new, ModBlocks.REDSTONE_DIRT_DETECTOR);
    public static final RegistryObject<BlockEntityType<TileDetectorRedstoneFarmland>> DETECTOR_REDSTONE_FARMLAND = register("redstone_farmland_detector", TileDetectorRedstoneFarmland::new, ModBlocks.REDSTONE_FARMLAND_DETECTOR);
    static void register() {}

    private static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> register(String name, BlockEntityType.BlockEntitySupplier<T> factory, RegistryObject<? extends Block> block) {
        return RegistrationManager.TILE_ENTITIES.register(name, () -> BlockEntityType.Builder.of(factory, block.get()).build(null));
    }
}
