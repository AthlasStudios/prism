plugins {
    id("prism-publish")
}

val versionModules = project(":brigadier:brigadier-minecraft").subprojects
    .filter { it.name != "brigadier-paper" }

dependencies {
    "shade"(project(":platform:spigot"))
    "shade"(project(":core"))
    "shade"(project(":platform:bukkit"))
    "shade"(project(":brigadier:brigadier-api"))
    "shade"(project(":brigadier:brigadier-minecraft"))
    versionModules.forEach { "shade"(project(it.path)) }
}