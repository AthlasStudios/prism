package dev.gurwi.prism.argument;

import java.util.Map;
import java.util.Optional;

public class CommandArguments {

    private final Map<String, Object> requiredArguments;
    private final Map<String, Object> optionalArguments;

    public CommandArguments(Map<String, Object> requiredArguments, Map<String, Object> optionalArguments) {
        this.requiredArguments = requiredArguments;
        this.optionalArguments = optionalArguments;
    }

    public <T> T get(String key) {
        return (T) requiredArguments.get(key);
    }

    public <T> Optional<T> getOptional(String key) {
        return Optional.ofNullable((T) optionalArguments.get(key));
    }

    public <T> T getOptional(String key, T defaultValue) {
        return (T) optionalArguments.getOrDefault(key, defaultValue);
    }

}
