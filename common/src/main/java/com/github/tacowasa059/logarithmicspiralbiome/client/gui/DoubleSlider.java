package com.github.tacowasa059.logarithmicspiralbiome.client.gui;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;
import java.util.function.Function;

public class DoubleSlider extends AbstractSliderButton {
    private final Component prefix;
    private final double min;
    private final double max;
    private final double step;
    private final String format;
    private final Function<Double, Component> messageProvider;
    private final Consumer<Double> onValueChange;

    public DoubleSlider(int x, int y, int width, int height, Component prefix, double min, double max, double step, double initialValue, String format, Consumer<Double> onValueChange) {
        this(x, y, width, height, prefix, min, max, step, initialValue, format, null, onValueChange);
    }

    public DoubleSlider(int x, int y, int width, int height, Component prefix, double min, double max, double step, double initialValue, Function<Double, Component> messageProvider, Consumer<Double> onValueChange) {
        this(x, y, width, height, prefix, min, max, step, initialValue, null, messageProvider, onValueChange);
    }

    private DoubleSlider(int x, int y, int width, int height, Component prefix, double min, double max, double step, double initialValue, String format, Function<Double, Component> messageProvider, Consumer<Double> onValueChange) {
        super(x, y, width, height, Component.empty(), toNormalized(initialValue, min, max));
        this.prefix = prefix;
        this.min = min;
        this.max = max;
        this.step = step;
        this.format = format;
        this.messageProvider = messageProvider;
        this.onValueChange = onValueChange;
        this.updateMessage();
    }

    private static double toNormalized(double value, double min, double max) {
        return Math.clamp((value - min) / (max - min), 0.0D, 1.0D);
    }

    public double getActualValue() {
        double raw = this.min + this.value * (this.max - this.min);
        if (this.step > 0.0D) {
            raw = Math.round(raw / this.step) * this.step;
        }
        return Math.clamp(raw, this.min, this.max);
    }

    public void setActualValue(double actualValue) {
        this.value = toNormalized(actualValue, this.min, this.max);
        this.updateMessage();
    }

    @Override
    protected void updateMessage() {
        if (this.messageProvider != null) {
            this.setMessage(this.messageProvider.apply(this.getActualValue()));
        } else {
            this.setMessage(Component.translatable(
                    "createWorld.customize.logarithmic_spiral.slider_format",
                    this.prefix,
                    String.format(this.format != null ? this.format : "%.1f", this.getActualValue())
            ));
        }
    }

    @Override
    protected void applyValue() {
        if (this.onValueChange != null) {
            this.onValueChange.accept(this.getActualValue());
        }
    }
}
