plugins {
    id("java")
    id("prism.spigot-reobf")
}

dependencies {
    implementation(project(":reflection"))
    compileOnly(project(":brigadier:brigadier-minecraft"))

    spigotNms("1.19.4-R0.1-SNAPSHOT", NMSMapping.MOJANG)
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(17)
}