package dev.gurwi.prism.bukkit.argument;

import dev.gurwi.prism.bukkit.argument.base.PositionArgument;

public class BlockPositionArgument extends PositionArgument<BlockPositionArgument> {

    public BlockPositionArgument(String name) {
        super(name, false, true);
    }

}
