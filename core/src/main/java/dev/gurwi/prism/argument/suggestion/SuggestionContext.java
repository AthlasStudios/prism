package dev.gurwi.prism.argument.suggestion;

public class SuggestionContext<Source> {

    private final Source source;
    private final String currentInput;
    private final String currentRawArg;

    public SuggestionContext(Source source, String currentInput, String currentRawArg) {
        this.source = source;
        this.currentInput = currentInput;
        this.currentRawArg = currentRawArg;
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

}
