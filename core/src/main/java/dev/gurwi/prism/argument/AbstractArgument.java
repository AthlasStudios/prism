package dev.gurwi.prism.argument;

import dev.gurwi.prism.exception.ArgumentParseException;
import dev.gurwi.prism.requirement.UsageRequirement;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unchecked")
public abstract class AbstractArgument<Impl, T, Source, Suggestions extends SuggestionsSupplier> {

    private final String name;

    private Suggestions suggestionsSupplier;
    private UsageRequirement<Source> requirement = UsageRequirement.none();

    public AbstractArgument(String name, Suggestions defaultSuggestions) {
        this.name = name;
        this.suggestionsSupplier = defaultSuggestions;
    }

    public Impl replaceSuggestions(Suggestions suggestionsSupplier) {
        this.suggestionsSupplier = suggestionsSupplier;
        return self();
    }

    public Impl withRequirement(UsageRequirement<Source> requirement) {
        this.requirement = this.requirement.and(requirement);
        return self();
    }

    public String getName() {
        return name;
    }

    public Suggestions getSuggestionsSupplier() {
        return suggestionsSupplier;
    }

    public UsageRequirement<Source> getRequirement() {
        return requirement;
    }

    public abstract T parse(@NotNull Source source, @NotNull String value) throws ArgumentParseException;

    @SuppressWarnings("unchecked")
    public Impl self() {
        return (Impl) this;
    }

}
