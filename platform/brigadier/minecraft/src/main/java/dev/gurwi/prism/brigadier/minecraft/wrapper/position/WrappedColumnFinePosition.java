package dev.gurwi.prism.brigadier.minecraft.wrapper.position;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public record WrappedColumnFinePosition(double x, double z) {

    @Contract("_ -> new")
    public @NotNull WrappedFinePosition toPosition(final double y) {
        return WrappedPosition.fine(this.x(), y, this.z());
    }


}
