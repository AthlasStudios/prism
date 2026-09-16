plugins {
    id("java-library")
    id("com.gradleup.shadow") version "9.4.1" apply false
}

allprojects {
    group = "dev.gurwi"
    version = project.rootProject.version

    apply(plugin = "java-library")

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(17))
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