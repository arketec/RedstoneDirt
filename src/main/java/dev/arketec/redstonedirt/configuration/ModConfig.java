package dev.arketec.redstonedirt.configuration;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;

public class ModConfig {
    public static final ForgeConfigSpec CONFIG_SPEC;

    public static ForgeConfigSpec.IntValue detectorRange;

    static {

        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.comment("Config for redstone dirt").push("detectors");

        detectorRange = builder
                .comment("The number of blocks above the detector will check for saplings or crops:")
                .defineInRange("detectorRange", 1 , 1, 3);
        builder.pop();

        CONFIG_SPEC = builder.build();
    }

    public static void loadConfig(ForgeConfigSpec spec, Path path)
    {
        final CommentedFileConfig configData = CommentedFileConfig.builder(path).sync().autosave().writingMode(WritingMode.REPLACE).build();

        configData.load();

        spec.setConfig(configData);
    }
}
