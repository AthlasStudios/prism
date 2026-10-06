pluginManagement {

    repositories {
        gradlePluginPortal()

        maven("https://repo.papermc.io/repository/maven-public/")
    }

}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}

rootProject.name = "prism"

include(":core")
include(":reflection")
include(":test:plugin")
include(":platform:bukkit")
include(":platform:spigot")
include(":platform:paper")
include(":platform:brigadier")