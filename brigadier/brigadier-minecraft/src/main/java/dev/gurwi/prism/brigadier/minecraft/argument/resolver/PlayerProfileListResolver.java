package dev.gurwi.prism.brigadier.minecraft.argument.resolver;

import dev.gurwi.prism.brigadier.minecraft.argument.ArgumentResolver;
import dev.gurwi.prism.brigadier.minecraft.wrapper.WrappedGameProfile;

import java.util.Collection;

public interface PlayerProfileListResolver extends ArgumentResolver<Collection<WrappedGameProfile>> {
}
