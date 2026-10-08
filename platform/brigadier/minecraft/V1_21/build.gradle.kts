plugins {
    id("java")
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.24"
}

dependencies {
    implementation(project(":reflection"))
    implementation(project(":platform:brigadier:minecraft"))
    paperweight.paperDevBundle("1.21.11-R0.1-SNAPSHOT")
}

java {
    toolchain.languageVersion.set(
        JavaLanguageVersion.of(21)
    )
}