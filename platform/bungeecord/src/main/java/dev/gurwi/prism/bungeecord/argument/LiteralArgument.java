package dev.gurwi.prism.bungeecord.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import net.md_5.bungee.api.CommandSender;
import dev.gurwi.prism.exception.ArgumentParseException;
import dev.gurwi.prism.bungeecord.argument.base.BungeecordArgument;
import org.jetbrains.annotations.NotNull;

public class LiteralArgument extends BungeecordArgument<LiteralArgument, Void> {

    public LiteralArgument(String name) {
        super(name);
        super.setListed(false);
    }

    @Override
    public Void parse(@NotNull CommandSender commandSource, @NotNull String value) throws ArgumentParseException {
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
