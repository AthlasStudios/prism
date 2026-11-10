package dev.gurwi.prism.argument;

public class ParsedArgument<T> {

    private final String raw;
    private final T parsed;

    public ParsedArgument(String raw, T parsed) {
        this.raw = raw;
        this.parsed = parsed;
    }

    public String getRaw() {
        return raw;
    }

    public T getParsed() {
        return parsed;
    }

}
