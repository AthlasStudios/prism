plugins {
    id("java-library")
    id("com.gradleup.shadow") version "9.4.1" apply false
}

allprojects {
    group = "dev.gurwi"
    version = project.rootProject.version

    plugins.apply("java-library")

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
        compileOnly("org.jetbrains:annotations:26.0.2")

        testImplementation(platform("org.junit:junit-bom:6.0.0"))
        testImplementation("org.junit.jupiter:junit-jupiter")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    }

    plugins.withId("java-test-fixtures") {
        dependencies {
            "testFixturesImplementation"(platform("org.junit:junit-bom:6.0.0"))
            "testFixturesImplementation"("org.junit.jupiter:junit-jupiter")
        }
    }

    tasks.test {
        useJUnitPlatform()
    }
}