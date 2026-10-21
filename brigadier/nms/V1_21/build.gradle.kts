plugins {
    id("java")
    id("prism.spigot-reobf")
}

dependencies {
    compileOnly(project(":brigadier:brigadier-minecraft"))

    spigotNms("1.21.11-R0.1-SNAPSHOT", NMSMapping.MOJANG)
}

configurations.configureEach {
    if (isCanBeConsumed) {
        attributes.attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 8)
    }
}

java {
    toolchain.languageVersion.set(
        JavaLanguageVersion.of(21)
    )
}