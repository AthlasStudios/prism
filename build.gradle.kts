plugins {
    id("java-library")
    id("com.gradleup.shadow") version "9.4.1" apply false
}

val targetJavaVersion = 11
val testJavaVersion = 21

rootProject.extra["targetJavaVersion"] = targetJavaVersion
rootProject.extra["testJavaVersion"] = testJavaVersion

allprojects {
    group = project.rootProject.group
    version = project.rootProject.version

    plugins.apply("java-library")

    java {
        toolchain.languageVersion.set(
            JavaLanguageVersion.of(targetJavaVersion)
        )
    }

    repositories {
        mavenCentral()

        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://repo.papermc.io/repository/maven-snapshots/")
        maven("https://repo.codemc.io/repository/nms/")
        maven("https://libraries.minecraft.net")
    }

    configurations.configureEach {
        exclude(group = "org.jspecify", module = "jspecify")
    }
}

subprojects {
    dependencies {
        compileOnly("org.jetbrains:annotations:26.0.2")

        testImplementation(platform("org.junit:junit-bom:6.0.0"))
        testImplementation("org.junit.jupiter:junit-jupiter")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    }

    tasks.withType<JavaCompile>().configureEach {
        if (name.contains("Test")) {
            javaCompiler.set(
                javaToolchains.compilerFor {
                    languageVersion.set(
                        JavaLanguageVersion.of(testJavaVersion)
                    )
                }
            )
        }
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()

        javaLauncher.set(
            javaToolchains.launcherFor {
                languageVersion.set(
                    JavaLanguageVersion.of(testJavaVersion)
                )
            }
        )
    }

    plugins.withId("java-test-fixtures") {
        dependencies {
            "testFixturesImplementation"(
                platform("org.junit:junit-bom:6.0.0")
            )
            "testFixturesImplementation"(
                "org.junit.jupiter:junit-jupiter"
            )
        }

        configurations {
            named("testFixturesCompileClasspath") {
                attributes {
                    attribute(
                        TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE,
                        testJavaVersion
                    )
                }
            }

            named("testFixturesRuntimeClasspath") {
                attributes {
                    attribute(
                        TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE,
                        testJavaVersion
                    )
                }
            }
        }
    }
}