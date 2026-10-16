package dev.gurwi.prism.command.builder;

import dev.gurwi.prism.requirement.UsageRequirement;
import org.jetbrains.annotations.NotNull;

public interface RequirementContainerBuilder<Impl, Requirement extends UsageRequirement<?>> {

    Impl withRequirement(@NotNull Requirement requirement);

}
