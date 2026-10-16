package dev.gurwi.prism.jda.argument;

import dev.gurwi.prism.argument.AbstractArgument;
import net.dv8tion.jda.api.entities.User;

public abstract class JDAArgument<Impl extends JDAArgument<Impl, T>, T> extends AbstractArgument<Impl, T, User, JDASuggestionsSupplier<T>> {

    public JDAArgument(String name, JDASuggestionsSupplier<T> defaultSuggestions) {
        super(name, defaultSuggestions);
    }

}
