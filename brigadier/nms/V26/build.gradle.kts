plugins {
    id("java")
    id("prism.spigot-reobf")
}

dependencies {
    compileOnly(project(":brigadier:brigadier-minecraft"))

    spigotNms("26.3-R0.1-SNAPSHOT")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}