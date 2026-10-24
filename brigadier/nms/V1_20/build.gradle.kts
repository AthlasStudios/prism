plugins {
    id("java")
    id("prism.spigot-reobf")
}

dependencies {
    compileOnly(project(":brigadier:brigadier-minecraft"))

    spigotNms("1.20.6-R0.1-SNAPSHOT", NMSMapping.MOJANG)
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(21)
}