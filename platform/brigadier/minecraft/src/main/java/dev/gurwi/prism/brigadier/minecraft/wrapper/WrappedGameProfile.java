package dev.gurwi.prism.brigadier.minecraft.wrapper;

import com.mojang.authlib.GameProfile;

import java.util.Objects;
import java.util.UUID;

public final class WrappedGameProfile {

    private final GameProfile handle;

    public WrappedGameProfile(GameProfile handle) {
        this.handle = Objects.requireNonNull(handle, "handle");
    }

    public UUID getId() {
        return handle.getId();
    }

    public String getName() {
        return handle.getName();
    }

}