plugins {
    id("java")
    id("java-test-fixtures")
    id("prism-publish")
}

val testJavaVersion = rootProject.extra["testJavaVersion"] as Int

dependencies {
    api(project(":core"))
    "shade"(project(":reflection"))

    compileOnly("org.spigotmc:spigot-api:1.13.2-R0.1-SNAPSHOT")

    testFixturesApi("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")
    testFixturesApi("org.mockbukkit.mockbukkit:mockbukkit-v1.21:4.0.0")
}

java {
    toolchain.languageVersion.set(
        JavaLanguageVersion.of(11)
    )
}

val javaComponent = components["java"] as AdhocComponentWithVariants
javaComponent.withVariantsFromConfiguration(configurations["testFixturesApiElements"]) { skip() }
javaComponent.withVariantsFromConfiguration(configurations["testFixturesRuntimeElements"]) { skip() }

plugins.withId("java-test-fixtures") {
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

    tasks.withType<JavaCompile>().configureEach {
        if (name.contains("TestFixtures")) {
            javaCompiler.set(
                javaToolchains.compilerFor {
                    languageVersion.set(JavaLanguageVersion.of(testJavaVersion))
                }
            )
        }
    }
}