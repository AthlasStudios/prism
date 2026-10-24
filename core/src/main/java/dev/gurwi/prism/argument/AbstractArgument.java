package dev.gurwi.prism.argument;

import dev.gurwi.prism.argument.suggestion.SuggestionsSupplier;
import dev.gurwi.prism.command.builder.RequirementContainerBuilder;
import dev.gurwi.prism.exception.ArgumentParseException;
import dev.gurwi.prism.requirement.UsageRequirement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@SuppressWarnings("unchecked")
public abstract class AbstractArgument<Impl extends AbstractArgument<Impl, T, Source, Suggestions>, T, Source, Suggestions extends SuggestionsSupplier<Source, ?>> implements RequirementContainerBuilder<Impl, UsageRequirement<Source>> {

    private final String name;
    private final List<Suggestions> includedSuggestions = new ArrayList<>();

    protected boolean listed = true;
    private @Nullable Suggestions replacedSuggestions;
    private UsageRequirement<Source> requirement = UsageRequirement.none();

    public AbstractArgument(String name) {
        this.name = name;
    }

    public Impl setListed(boolean listed) {
        this.listed = listed;
        return self();
    }

    public Impl replaceSuggestions(@Nullable Suggestions suggestions) {
        this.replacedSuggestions = suggestions;
        return self();
    }

    public Impl includeSuggestions(@NotNull Suggestions suggestions) {
        this.includedSuggestions.add(Objects.requireNonNull(suggestions, "suggestions"));
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

    public @NotNull Optional<Suggestions> getSuggestionsSupplier() {
        return Optional.ofNullable(replacedSuggestions);
    }

    public @NotNull @Unmodifiable List<Suggestions> getIncludedSuggestions() {
        return Collections.unmodifiableList(includedSuggestions);
    }

    public @NotNull @Unmodifiable List<Suggestions> getCustomSuggestions() {
        List<Suggestions> all = new ArrayList<>(includedSuggestions.size() + 1);

        if (replacedSuggestions != null) {
            all.add(replacedSuggestions);
        }

        all.addAll(includedSuggestions);
        return Collections.unmodifiableList(all);
    }

    public UsageRequirement<Source> getRequirement() {
        return requirement;
    }

    public boolean canUse(@NotNull Source source) {
        return requirement.test(source);
    }

    public abstract T parse(@NotNull Source source, @NotNull String value) throws ArgumentParseException;

    protected Impl self() {
        return (Impl) this;
    }

}