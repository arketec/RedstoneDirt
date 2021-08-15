package dev.arketec.redstonedirt;

import dev.arketec.redstonedirt.registration.RegistrationManager;
import net.minecraftforge.fml.common.Mod;

@Mod(RedstoneDirt.MODID)
public class RedstoneDirt {
    public static final String MODID = "redstonedirt";
    public static final String MOD_NAME = "Redstone Dirt";

    public RedstoneDirt() {
        RegistrationManager.register();
    }
}
