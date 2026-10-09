plugins {
    id("java")
}

dependencies {
    api(project(":platform:brigadier:minecraft"))
    compileOnly("io.papermc.paper:paper-api:1.20.6-R0.1-SNAPSHOT")
}

java {
    toolchain.languageVersion.set(
        JavaLanguageVersion.of(21)
    )
}