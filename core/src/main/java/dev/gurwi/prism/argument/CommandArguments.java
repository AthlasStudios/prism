package dev.gurwi.prism.argument;

import java.util.Map;
import java.util.Optional;

public class CommandArguments {

    private final Map<String, Object> requiredArgumentValues;
    private final Map<String, Object> optionalArgumentValues;

    public CommandArguments(Map<String, Object> requiredArgumentValues, Map<String, Object> optionalArgumentValues) {
        this.requiredArgumentValues = requiredArgumentValues;
        this.optionalArgumentValues = optionalArgumentValues;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) requiredArgumentValues.get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T> getOptional(String key) {
        return Optional.ofNullable((T) optionalArgumentValues.get(key));
    }

    @SuppressWarnings("unchecked")
    public <T> T getOptional(String key, T defaultValue) {
        return (T) optionalArgumentValues.getOrDefault(key, defaultValue);
    }

}
