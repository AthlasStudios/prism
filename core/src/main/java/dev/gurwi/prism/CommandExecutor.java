package dev.gurwi.prism;

import dev.gurwi.prism.argument.CommandArguments;

public interface CommandExecutor<S> {

    void run(S source, CommandArguments args);

}
