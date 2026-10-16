plugins {
    id("prism-publish")
}

val versionModules = project(":platform:brigadier:minecraft").subprojects
    .filter { it.name != "brigadier-paper" }

dependencies {
    "shade"(project(":platform:spigot"))
    "shade"(project(":core"))
    "shade"(project(":platform:bukkit"))
    "shade"(project(":platform:brigadier"))
    "shade"(project(":platform:brigadier:minecraft"))
    versionModules.forEach { "shade"(project(it.path)) }
}