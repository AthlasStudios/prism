package dev.gurwi.prism.argument.suggestion;

import dev.gurwi.prism.argument.CommandArguments;

public class SuggestionContext<Source> {

    private final Source source;
    private final String currentInput;
    private final String currentRawArg;
    private final CommandArguments<?, Source> previousArgs;
    private final boolean nativeSuggestions;

    public SuggestionContext(Source source, String currentInput, String currentRawArg, CommandArguments<?, Source> previousArgs) {
        this(source, currentInput, currentRawArg, previousArgs, false);
    }

    public SuggestionContext(Source source, String currentInput, String currentRawArg, CommandArguments<?, Source> previousArgs, boolean nativeSuggestions) {
        this.source = source;
        this.currentInput = currentInput;
        this.currentRawArg = currentRawArg;
        this.previousArgs = previousArgs;
        this.nativeSuggestions = nativeSuggestions;
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

    public CommandArguments<?, Source> getPreviousArgs() {
        return previousArgs;
    }

    public boolean hasNativeSuggestions() {
        return nativeSuggestions;
    }

}
