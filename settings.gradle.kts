pluginManagement {
    repositories {
        gradlePluginPortal()

        maven("https://repo.papermc.io/repository/maven-public/")
    }

    includeBuild("build-logic/spigot-nms")
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "prism"

include(":core")
include(":reflection")
include(":platform:bukkit")
include(":platform:spigot")
include(":platform:paper")
include(":platform:velocity")
include(":platform:jda")

include(":brigadier:brigadier-api")
include(":brigadier:brigadier-minecraft")
include(":brigadier:brigadier-paper")
include(":brigadier:nms:V1_13")
include(":brigadier:nms:V1_14")
include(":brigadier:nms:V1_15")
include(":brigadier:nms:V1_16")
include(":brigadier:nms:V1_17")
include(":brigadier:nms:V1_18")
include(":brigadier:nms:V1_19")
include(":brigadier:nms:V1_20")
include(":brigadier:nms:V1_21")
include(":brigadier:nms:V26")

include(
    ":examples:paper-plugin",
    ":examples:velocity-plugin",
)

include(
    ":plugin:bukkit-plugin",
    ":plugin:bukkit-plugin:plugin",
    ":plugin:bukkit-plugin:api"
)

include(":bundles:paper-bundle", ":bundles:spigot-bundle")