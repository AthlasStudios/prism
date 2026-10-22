package dev.gurwi.prism.velocity.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.velocitypowered.api.command.CommandSource;
import dev.gurwi.prism.exception.ArgumentParseException;
import dev.gurwi.prism.velocity.argument.base.VelocityArgument;
import org.jetbrains.annotations.NotNull;

public class GreedyStringArgument extends VelocityArgument<GreedyStringArgument, String> {

    public GreedyStringArgument(String name) {
        super(name);
    }

    @Override
    public String parse(@NotNull CommandSource commandSource, @NotNull String value) throws ArgumentParseException {
        return value;
    }

    @Override
    public ArgumentType<String> getArgumentType() {
        return StringArgumentType.greedyString();
    }

}
