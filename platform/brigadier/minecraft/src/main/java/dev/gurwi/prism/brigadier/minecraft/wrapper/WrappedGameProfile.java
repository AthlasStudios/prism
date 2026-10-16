package dev.gurwi.prism.brigadier.minecraft.wrapper;

import com.mojang.authlib.GameProfile;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

public record WrappedGameProfile(UUID id, String name) {

    public WrappedGameProfile(UUID id, String name) {
        this.id = Objects.requireNonNull(id, "id");
        this.name = Objects.requireNonNull(name, "name");
    }

    public WrappedGameProfile(@NotNull GameProfile handle) {
        this(handle.getId(), handle.getName());
    }

}