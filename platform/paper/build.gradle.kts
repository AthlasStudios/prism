plugins {
    id("java")
}

dependencies {
    api(project(":platform:bukkit"))
    implementation(project(":platform:brigadier:minecraft:brigadier-paper"))

    compileOnly("io.papermc.paper:paper-api:26.3.build.+")
}

java {
    toolchain.languageVersion.set(
        JavaLanguageVersion.of(25)
    )
}