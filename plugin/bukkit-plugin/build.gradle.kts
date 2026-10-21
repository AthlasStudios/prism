plugins {
    id("java-library")
//    id("xyz.jpenilla.run-paper") version "3.1.0" apply false
    id("com.modrinth.minotaur") version "2.+" apply false

    // Other Resource Factories -> https://github.com/jpenilla/resource-factory
    id("xyz.jpenilla.resource-factory-bukkit-convention") version "1.3.1" apply false
    id("xyz.jpenilla.resource-factory-paper-convention") version "1.3.1" apply false
}

subprojects {
    group = "club.athlas"

    dependencies {
        compileOnly("org.spigotmc:spigot-api:1.12.2-R0.1-SNAPSHOT")
    }
}