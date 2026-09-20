package dev.gurwi.prism.command;

import dev.gurwi.prism.argument.CommandArguments;

public interface CommandExecutor<Source> {

    void run(Source source, CommandArguments args);

}
