package dev.gurwi.prism.velocity.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import com.velocitypowered.api.command.CommandSource;
import dev.gurwi.prism.exception.ArgumentParseException;
import dev.gurwi.prism.velocity.argument.base.VelocityArgument;
import org.jetbrains.annotations.NotNull;

public class LiteralArgument extends VelocityArgument<LiteralArgument, Void> {

    public LiteralArgument(String name) {
        super(name);
        super.setListed(false);
    }

    @Override
    public Void parse(@NotNull CommandSource commandSource, @NotNull String value) throws ArgumentParseException {
        return null;
    }

    @Override
    public LiteralArgument setListed(boolean listed) {
        throw new IllegalArgumentException("Literal arguments cannot be listed!");
    }

    @Override
    public ArgumentType<Void> getArgumentType() {
        return null;
    }

}
