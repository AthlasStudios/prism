plugins {
    id("java")
    id("prism-publish")
}

dependencies {
    api(project(":platform:bukkit"))
    implementation(project(":platform:brigadier:minecraft:brigadier-paper"))

    compileOnly("com.destroystokyo.paper:paper-api:1.13.2-R0.1-SNAPSHOT")
}