package dev.gurwi.prism.jda.argument;

import dev.gurwi.prism.argument.suggestion.SuggestionsSupplier;
import net.dv8tion.jda.api.entities.User;

import java.util.Collection;

public interface JDASuggestionsSupplier<T> extends SuggestionsSupplier<User, Collection<T>> {

//    @FunctionalInterface
//    interface Sync<T> extends JDASuggestionsSupplier<T>, SuggestionsSupplier.Sync<T, User> {
//
//    }
//
//    @FunctionalInterface
//    interface Async<T> extends JDASuggestionsSupplier<T>, SuggestionsSupplier.Async<T, User> {
//
//    }

}
