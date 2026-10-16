plugins {
    id("prism-publish")
}

dependencies {
    "shade"(project(":core"))
    "shade"(project(":platform:bukkit"))
    "shade"(project(":platform:brigadier:minecraft:brigadier-paper"))
}