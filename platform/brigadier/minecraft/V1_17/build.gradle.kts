plugins {
    id("java")
    id("prism.spigot-reobf")
//    id("io.papermc.paperweight.userdev") version "2.0.0-beta.24"
}

dependencies {
    implementation(project(":reflection"))
    compileOnly(project(":platform:brigadier:minecraft"))
//    paperweight.paperDevBundle("1.17.1-R0.1-SNAPSHOT")
}

spigotNms {
    version.set("1.17.1-R0.1-SNAPSHOT")
    mappings.set(NmsMappings.MOJANG)
}

configurations.configureEach {
    if (isCanBeConsumed) {
        attributes.attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 8)
    }
}

java {
    toolchain.languageVersion.set(
        JavaLanguageVersion.of(17) // TODO -> FIX AND SWITCH TO 16
    )
}