package dev.arketec.redstonedirt;

import dev.arketec.redstonedirt.configuration.ModConfig;
import dev.arketec.redstonedirt.registration.RegistrationManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;

@Mod(RedstoneDirt.MODID)
public class RedstoneDirt {
    public static final String MODID = "redstonedirt";
    public static final String MOD_NAME = "Redstone Dirt";

    public RedstoneDirt() {
        ModConfig.loadConfig(ModConfig.CONFIG_SPEC, FMLPaths.CONFIGDIR.get().resolve("redstone-dirt.toml"));
        RegistrationManager.register();
    }
}
