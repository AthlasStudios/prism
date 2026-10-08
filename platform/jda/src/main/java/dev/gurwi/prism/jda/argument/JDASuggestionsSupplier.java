package dev.gurwi.prism.jda.argument;

import dev.gurwi.prism.argument.SuggestionsSupplier;
import net.dv8tion.jda.api.entities.User;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public interface JDASuggestionsSupplier<T> extends SuggestionsSupplier {

    @FunctionalInterface
    interface Sync<T> extends JDASuggestionsSupplier<T>, SuggestionsSupplier.Sync<T, User> {

    }

    @FunctionalInterface
    interface Async<T> extends JDASuggestionsSupplier<T>, SuggestionsSupplier.Async<T, User> {

    }

}
