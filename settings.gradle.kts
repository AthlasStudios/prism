pluginManagement {

    repositories {
        gradlePluginPortal()

        maven("https://repo.papermc.io/repository/maven-public/")
    }

    plugins {
        kotlin("jvm") version "2.4.21"
    }

    includeBuild("gradle-plugin/spigot-nms")
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "prism"

include(":core")
include(":reflection")
include(":test:plugin")
include(":platform:bukkit")
include(":platform:spigot")
include(":platform:paper")
include(":platform:brigadier")
include(":platform:jda")

include(":platform:brigadier:minecraft")
include(":platform:brigadier:minecraft:V1_13")
include(":platform:brigadier:minecraft:V1_14")
include(":platform:brigadier:minecraft:V1_15")
include(":platform:brigadier:minecraft:V1_16")
include(":platform:brigadier:minecraft:V1_17")
include(":platform:brigadier:minecraft:V1_18")
include(":platform:brigadier:minecraft:V1_19")
include(":platform:brigadier:minecraft:V1_20")
include(":platform:brigadier:minecraft:V1_21")
include(":platform:brigadier:minecraft:V26")
include(":platform:brigadier:minecraft:brigadier-paper")