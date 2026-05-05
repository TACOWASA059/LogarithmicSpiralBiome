package com.github.tacowasa059.logarithmicspiralbiome;

import com.github.tacowasa059.logarithmicspiralbiome.registry.SpiralBiomeSources;
import com.github.tacowasa059.logarithmicspiralbiome.registry.SpiralFeatures;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Logarithmicspiralbiome.MODID)
public class Logarithmicspiralbiome {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "logarithmicspiralbiome";

    public Logarithmicspiralbiome() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        SpiralBiomeSources.register(modEventBus);
        SpiralFeatures.register(modEventBus);
    }
}
