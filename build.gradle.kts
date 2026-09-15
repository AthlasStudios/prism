plugins {
    id("java-library")
    id("com.gradleup.shadow") version "9.4.1" apply false
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.21" apply false
    id("xyz.jpenilla.run-paper") version "3.0.2" apply false
    id("com.modrinth.minotaur") version "2.+" apply false
}

allprojects {
    group = "dev.gurwi"
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
    }

    dependencies {
        // libs
        compileOnly("org.jetbrains:annotations:26.0.2")
    }
}