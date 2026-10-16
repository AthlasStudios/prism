plugins {
    id("prism-publish")
}

dependencies {
    "shade"(project(":platform:paper"))
    "shade"(project(":core"))
    "shade"(project(":platform:bukkit"))
    "shade"(project(":platform:brigadier"))
    "shade"(project(":platform:brigadier:minecraft"))
    "shade"(project(":platform:brigadier:minecraft:brigadier-paper"))
}