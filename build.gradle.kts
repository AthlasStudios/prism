plugins {
    id("java-library")
    id("com.gradleup.shadow") version "9.4.1" apply false
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.21" apply false
    id("xyz.jpenilla.run-paper") version "3.0.2" apply false
    id("com.modrinth.minotaur") version "2.+" apply false

    // Other Resource Factories -> https://github.com/jpenilla/resource-factory
    id("xyz.jpenilla.resource-factory-bukkit-convention") version "1.3.1" apply false
    id("xyz.jpenilla.resource-factory-paper-convention") version "1.3.1" apply false
    id("xyz.jpenilla.resource-factory-bungee-convention") version "1.3.1" apply false
    id("xyz.jpenilla.resource-factory-velocity-convention") version "1.3.1" apply false
}

allprojects {
    group = "club.athlas"
    version = project.rootProject.version

    apply(plugin = "java-library")

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    }
}

subprojects {
    repositories {
        mavenCentral()
        mavenLocal()

//        maven("https://repo.athlas.club/releases") {
//            credentials {
//                username = findProperty("athlasMavenUser") as String?
//                password = findProperty("athlasMavenPassword") as String?
//            }
//
//            authentication {
//                create<BasicAuthentication>("basic")
//            }
//        }

        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://repo.papermc.io/repository/maven-public/")
    }

    dependencies {
        compileOnly("org.spigotmc:spigot-api:1.21.5-R0.1-SNAPSHOT")

        // or use Paper API:
        //compileOnly("io.papermc.paper:paper-api:1.21.5-R0.1-SNAPSHOT")

        // libs
        compileOnly("org.jetbrains:annotations:26.0.2")
    }
}