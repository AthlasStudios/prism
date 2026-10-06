package dev.gurwi.prism.brigadier;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.jetbrains.annotations.NotNull;

public interface BrigadierCompatibleArgument<T> {

    @NotNull
    ArgumentType<?> brigadierType();

    @SuppressWarnings("unchecked")
    default <S> T fromBrigadier(@NotNull CommandContext<S> context, @NotNull String node) throws CommandSyntaxException {
        return (T) context.getArgument(node, Object.class);
    }

}
