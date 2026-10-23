package com.github.tacowasa059.logarithmicspiralbiome.mixin;

import com.github.tacowasa059.logarithmicspiralbiome.client.gui.SpiralPresetConfigScreen;
import net.minecraft.client.gui.screens.worldselection.PresetEditor;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldCreationUiState.class)
public abstract class WorldCreationUiStateMixin {
    @Shadow
    public abstract WorldCreationUiState.WorldTypeEntry getWorldType();

    @Inject(method = "getPresetEditor", at = @At("HEAD"), cancellable = true)
    private void logarithmicspiralbiome$getPresetEditor(CallbackInfoReturnable<PresetEditor> cir) {
        WorldCreationUiState.WorldTypeEntry worldType = this.getWorldType();
        if (worldType != null) {
            Holder<WorldPreset> preset = worldType.preset();
            if (preset != null) {
                preset.unwrapKey().ifPresent(key -> {
                    if ("logarithmicspiralbiome".equals(key.identifier().getNamespace())
                            && "logarithmic_spiral".equals(key.identifier().getPath())) {
                        cir.setReturnValue((createWorldScreen, worldCreationContext) ->
                                new SpiralPresetConfigScreen(createWorldScreen, worldCreationContext));
                    }
                });
            }
        }
    }
}
