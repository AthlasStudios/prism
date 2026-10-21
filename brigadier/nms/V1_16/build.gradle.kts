plugins {
    id("java")
    id("prism.spigot-reobf")
}

dependencies {
    implementation(project(":reflection"))
    compileOnly(project(":brigadier:brigadier-minecraft"))

    spigotNms("1.16.5-R0.1-SNAPSHOT")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(16))
}