plugins {
    id("prism-publish")
}

dependencies {
    "shade"(project(":platform:paper"))
    "shade"(project(":core"))
    "shade"(project(":platform:bukkit"))
    "shade"(project(":brigadier:brigadier-api"))
    "shade"(project(":brigadier:brigadier-minecraft"))
    "shade"(project(":brigadier:brigadier-paper"))
}