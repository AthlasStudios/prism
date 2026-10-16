plugins {
    id("java")
    id("com.gradleup.shadow")
    id("xyz.jpenilla.run-paper") version "3.1.0"
    id("xyz.jpenilla.resource-factory-bukkit-convention") version "1.3.1"
}

repositories {
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    implementation(project(":platform:paper"))
    implementation(project(":platform:brigadier:minecraft"))

    compileOnly("io.papermc.paper:paper-api:26.3.build.+")
}

tasks {
    assemble {
        dependsOn("shadowJar")
    }

    shadowJar {
        archiveFileName.set("${rootProject.name}-${rootProject.version}-PAPER-TEST.jar")
    }

    runServer {
        minecraftVersion("26.3")
    }
}

java {
    toolchain.languageVersion.set(
        JavaLanguageVersion.of(25)
    )
}

tasks.withType(xyz.jpenilla.runtask.task.AbstractRun::class) {
    javaLauncher = javaToolchains.launcherFor {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

bukkitPluginYaml {
    main.set("dev.gurwi.prism.test.plugin.TestPlugin")
    apiVersion.set("1.13")

    name.set(rootProject.name)
    version.set(rootProject.version.toString())
    description.set("Prism Spigot Implementation Test Plugin")
    authors.set(listOf("Gurwi"))

    foliaSupported.set(true)
}
