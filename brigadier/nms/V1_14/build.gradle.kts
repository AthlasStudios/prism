plugins {
    id("java")
    id("prism.spigot-reobf")
}

dependencies {
    implementation(project(":reflection"))
    compileOnly(project(":brigadier:brigadier-minecraft"))

    spigotNms("1.14.4-R0.1-SNAPSHOT")
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