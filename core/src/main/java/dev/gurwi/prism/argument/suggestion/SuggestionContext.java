package dev.gurwi.prism.argument.suggestion;

import dev.gurwi.prism.argument.CommandArguments;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class SuggestionContext<Source> {

    private final Source source;
    private final String currentInput;
    private final String currentRawArg;
    private final Supplier<? extends CommandArguments<?, Source>> previousArgs;
    private final boolean nativeSuggestions;
    private final Supplier<? extends List<String>> nativeSuggestionValues;

    public SuggestionContext(Source source, String currentInput, String currentRawArg, @NotNull CommandArguments<?, Source> previousArgs) {
        this(source, currentInput, currentRawArg, previousArgs, false);
    }

    public SuggestionContext(Source source, String currentInput, String currentRawArg, @NotNull CommandArguments<?, Source> previousArgs, boolean nativeSuggestions) {
        this(source, currentInput, currentRawArg, () -> previousArgs, nativeSuggestions, List::of, null);
    }

    public SuggestionContext(Source source, String currentInput, String currentRawArg, Supplier<? extends CommandArguments<?, Source>> previousArgs, boolean nativeSuggestions, Supplier<? extends List<String>> nativeSuggestionValues, Void marker) {
        this.source = source;
        this.currentInput = currentInput;
        this.currentRawArg = currentRawArg;
        this.previousArgs = previousArgs;
        this.nativeSuggestions = nativeSuggestions;
        this.nativeSuggestionValues = nativeSuggestionValues;
    }

    public static <Source> @NotNull SuggestionContext<Source> lazy(Source source, String currentInput, String currentRawArg, @NotNull Supplier<? extends CommandArguments<?, Source>> previousArgs, boolean nativeSuggestions) {
        return lazy(source, currentInput, currentRawArg, previousArgs, nativeSuggestions, List::of);
    }

    public static <Source> @NotNull SuggestionContext<Source> lazy(Source source, String currentInput, String currentRawArg, @NotNull Supplier<? extends CommandArguments<?, Source>> previousArgs, boolean nativeSuggestions, @NotNull Supplier<? extends List<String>> nativeSuggestionValues) {
        return new SuggestionContext<>(source, currentInput, currentRawArg, new Memoized<>(previousArgs), nativeSuggestions, new Memoized<>(nativeSuggestionValues), null);
    }

    public Source getSource() {
        return source;
    }

    public String getCurrentInput() {
        return currentInput;
    }

    public String getCurrentRawArg() {
        return currentRawArg;
    }

    public @NotNull CommandArguments<?, Source> getPreviousArgs() {
        return previousArgs.get();
    }

    public boolean hasNativeSuggestions() {
        return nativeSuggestions;
    }

    public @NotNull List<String> getNativeSuggestions() {
        return nativeSuggestionValues.get();
    }

    private static final class Memoized<T> implements Supplier<T> {

        private final Supplier<? extends T> delegate;
        private volatile T value;

        private Memoized(Supplier<? extends T> delegate) {
            this.delegate = Objects.requireNonNull(delegate);
        }

        @Override
        public T get() {
            T result = value;

            if (result == null) {
                synchronized (this) {
                    result = value;

                    if (result == null) {
                        result = Objects.requireNonNull(delegate.get());
                        value = result;
                    }
                }
            }

            return result;
        }
    }

}
