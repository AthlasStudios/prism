package dev.gurwi.prism.bukkit.argument;

import dev.gurwi.prism.argument.GreedyArgument;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GreedyStringArgument extends StringArgument implements GreedyArgument {

    public GreedyStringArgument(String name) {
        super(name);
    }

    @Override
    public String parse(String @NotNull [] args, int currentIndex) {
        List<String> msgArgs = new ArrayList<>(Arrays.asList(args).subList(currentIndex, args.length));
        return StringUtils.join(msgArgs, " ");
    }

}
