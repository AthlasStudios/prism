plugins {
    id("prism-publish")
}

val versionModules = rootProject.subprojects
    .filter { it.name.matches(Regex("V\\d+(_\\d+)*")) }

dependencies {
    "shade"(project(":platform:spigot"))
    "shade"(project(":core"))
    "shade"(project(":platform:bukkit"))
    "shade"(project(":brigadier:brigadier-api"))
    "shade"(project(":brigadier:brigadier-minecraft"))
    versionModules.forEach { "shade"(project(it.path)) }
}