package dev.gurwi.prism.brigadier;

import com.mojang.brigadier.CommandDispatcher;

public interface CommandDispatcherProvider<Source> {

    CommandDispatcher<Source> getDispatcher();

}
