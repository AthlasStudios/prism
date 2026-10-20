plugins {
    id("java")
    id("prism.spigot-reobf")
}

dependencies {
    implementation(project(":reflection"))
    compileOnly(project(":platform:brigadier:minecraft"))

    spigotNms("1.17.1-R0.1-SNAPSHOT", NMSMapping.MOJANG)
}

java {
    toolchain.languageVersion.set(
        JavaLanguageVersion.of(16)
    )
}

configurations.configureEach {
    if (isCanBeConsumed) {
        attributes.attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 8)
    }
}