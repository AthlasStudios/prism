package dev.gurwi.prism.bungeecord.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import net.md_5.bungee.api.CommandSender;
import dev.gurwi.prism.exception.ArgumentParseException;
import dev.gurwi.prism.bungeecord.argument.base.BungeecordArgument;
import org.jetbrains.annotations.NotNull;

public class BooleanArgument extends BungeecordArgument<BooleanArgument, Boolean> {

    public BooleanArgument(String name) {
        super(name);

        replaceSuggestions("true", "false");
    }

    @Override
    public Boolean parse(@NotNull CommandSender sender, @NotNull String value) throws ArgumentParseException {
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
