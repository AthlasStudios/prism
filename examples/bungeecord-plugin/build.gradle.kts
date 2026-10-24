plugins {
    id("java")
    id("com.gradleup.shadow")
    id("xyz.jpenilla.run-waterfall") version "3.1.0"
    id("xyz.jpenilla.resource-factory-bungee-convention") version "1.4.0"
}

dependencies {
    implementation(project(":platform:bungeecord"))
    implementation("com.mojang:brigadier:1.0.18")

    compileOnly("net.md-5:bungeecord-api:1.21-R0.2")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

bungeePluginYaml {
    main = "dev.gurwi.prism.example.bungeecord.plugin.BungeecordExample"
    author = "gurwi"
}

tasks {
    assemble {
        dependsOn("shadowJar")
    }

    withType<JavaCompile> {
        options.release.set(17)
    }

    test {
        javaLauncher.set(
            project.javaToolchains.launcherFor {
                languageVersion.set(JavaLanguageVersion.of(21))
            }
        )
    }

    shadowJar {
        archiveFileName.set("${rootProject.name}-${rootProject.version}-BUNGEECORD-EXAMPLE.jar")
    }

    runWaterfall {
        waterfallVersion("1.21")
    }
}

tasks.withType(xyz.jpenilla.runtask.task.AbstractRun::class) {
    javaLauncher = javaToolchains.launcherFor {
        languageVersion = JavaLanguageVersion.of(21)
    }
}