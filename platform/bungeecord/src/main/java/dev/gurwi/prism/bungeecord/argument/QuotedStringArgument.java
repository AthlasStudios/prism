package dev.gurwi.prism.bungeecord.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.md_5.bungee.api.CommandSender;
import dev.gurwi.prism.exception.ArgumentParseException;
import dev.gurwi.prism.bungeecord.argument.base.BungeecordArgument;
import org.jetbrains.annotations.NotNull;

public class QuotedStringArgument extends BungeecordArgument<QuotedStringArgument, String> {

    public QuotedStringArgument(String name) {
        super(name);
    }

    @Override
    public String parse(@NotNull CommandSender commandSource, @NotNull String value) throws ArgumentParseException {
        return value;
    }

    @Override
    public ArgumentType<String> getArgumentType() {
        return StringArgumentType.string();
    }

}
