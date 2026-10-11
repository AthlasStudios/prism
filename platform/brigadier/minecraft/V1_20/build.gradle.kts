plugins {
    id("java")
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.24"
}

dependencies {
    implementation(project(":reflection"))
    compileOnly(project(":platform:brigadier:minecraft"))
    paperweight.paperDevBundle("1.20.6-R0.1-SNAPSHOT")
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