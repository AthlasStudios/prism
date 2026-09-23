package dev.gurwi.prism.bukkit.argument.parser;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public final class ArgumentsParseResult {

    private final Map<String, Object> required;
    private final Map<String, Object> optional;

    public ArgumentsParseResult(@NotNull Map<String, Object> required, @NotNull Map<String, Object> optional) {
        this.required = Map.copyOf(required);
        this.optional = Map.copyOf(optional);
    }

    public @NotNull Map<String, Object> getRequired() {
        return required;
    }

    public @NotNull Map<String, Object> getOptional() {
        return optional;
    }

}