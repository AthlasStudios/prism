package dev.gurwi.prism.argument;

import dev.gurwi.prism.command.builder.RequirementContainerBuilder;
import dev.gurwi.prism.exception.ArgumentParseException;
import dev.gurwi.prism.requirement.UsageRequirement;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unchecked")
public abstract class AbstractArgument<Impl extends AbstractArgument<Impl, T, Source, Suggestions>, T, Source, Suggestions extends SuggestionsSupplier> implements RequirementContainerBuilder<Impl, UsageRequirement<Source>> {

    private final String name;

    protected boolean listed = true;
    private Suggestions suggestionsSupplier;
    private UsageRequirement<Source> requirement = UsageRequirement.none();

    public AbstractArgument(String name, Suggestions defaultSuggestions) {
        this.name = name;
        this.suggestionsSupplier = defaultSuggestions;
    }

    public Impl setListed(boolean listed) {
        this.listed = listed;
        return self();
    }

    public Impl replaceSuggestions(Suggestions suggestionsSupplier) {
        this.suggestionsSupplier = suggestionsSupplier;
        return self();
    }

    @Override
    public Impl withRequirement(@NotNull UsageRequirement<Source> requirement) {
        this.requirement = this.requirement.and(requirement);
        return self();
    }

    public String getName() {
        return name;
    }

    public boolean isListed() {
        return listed;
    }

    public Suggestions getSuggestionsSupplier() {
        return suggestionsSupplier;
    }

    public UsageRequirement<Source> getRequirement() {
        return requirement;
    }

    public boolean canUse(@NotNull Source source) {
        return requirement.test(source);
    }

    public abstract T parse(@NotNull Source source, @NotNull String value) throws ArgumentParseException;

    @SuppressWarnings("unchecked")
    protected Impl self() {
        return (Impl) this;
    }

}
