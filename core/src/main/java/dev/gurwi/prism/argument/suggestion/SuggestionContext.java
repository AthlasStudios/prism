package dev.gurwi.prism.argument.suggestion;

public class SuggestionContext<Source> {

    private final Source source;
    private final String currentInput;
    private final String currentRawArg;
    private final boolean nativeSuggestions;

    public SuggestionContext(Source source, String currentInput, String currentRawArg) {
        this(source, currentInput, currentRawArg, false);
    }

    public SuggestionContext(Source source, String currentInput, String currentRawArg, boolean nativeSuggestions) {
        this.source = source;
        this.currentInput = currentInput;
        this.currentRawArg = currentRawArg;
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

    public boolean hasNativeSuggestions() {
        return nativeSuggestions;
    }

}
