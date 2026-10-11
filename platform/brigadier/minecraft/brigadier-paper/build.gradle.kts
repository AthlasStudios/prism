plugins {
    id("java")
}

dependencies {
    api(project(":platform:brigadier:minecraft"))
    compileOnly("io.papermc.paper:paper-api:26.3.build.+")
}

configurations.configureEach {
    if (isCanBeConsumed) {
        attributes.attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 8)
    }
}

java {
    toolchain.languageVersion.set(
        JavaLanguageVersion.of(25)
    )
}