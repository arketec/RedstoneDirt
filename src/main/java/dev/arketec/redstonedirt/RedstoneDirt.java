package dev.arketec.redstonedirt;

import dev.arketec.redstonedirt.registration.ModBlocks;
import dev.arketec.redstonedirt.registration.ModColors;
import dev.arketec.redstonedirt.registration.RegistrationManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;

@Mod(RedstoneDirt.MODID)
public class RedstoneDirt {
    public static final String MODID = "redstonedirt";
    public static final String MOD_NAME = "Redstone Dirt";

    public RedstoneDirt() {

        RegistrationManager.register();
    }

//    @OnlyIn(Dist.CLIENT)
//    private void OnLoadComplete(final FMLLoadCompleteEvent event) {
//        ModColors.registerBlockColors();
//    }

}
