package dev.gurwi.prism.brigadier.minecraft.wrapper.position;

public class WrappedRotation {

    private final float yaw;
    private final float pitch;

    public WrappedRotation(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

}
