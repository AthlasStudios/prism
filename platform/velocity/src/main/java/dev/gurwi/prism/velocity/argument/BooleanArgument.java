package dev.gurwi.prism.velocity.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.velocitypowered.api.command.CommandSource;
import dev.gurwi.prism.exception.ArgumentParseException;
import dev.gurwi.prism.velocity.argument.base.VelocityArgument;
import org.jetbrains.annotations.NotNull;

public class BooleanArgument extends VelocityArgument<BooleanArgument, Boolean> {

    public BooleanArgument(String name) {
        super(name);

        replaceSuggestions("true", "false");
    }

    @Override
    public Boolean parse(@NotNull CommandSource sender, @NotNull String value) throws ArgumentParseException {
        if (value.equalsIgnoreCase("true")) {
            return true;
        }

        if (value.equalsIgnoreCase("false")) {
            return false;
        }

        throw new ArgumentParseException(this, value, "Expected a boolean got " + value);
    }

    @Override
    public ArgumentType<Boolean> getArgumentType() {
        return BoolArgumentType.bool();
    }

}
