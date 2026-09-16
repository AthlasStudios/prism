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
    implementation(project(":platform:spigot"))

    compileOnly("org.spigotmc:spigot-api:1.13.2-R0.1-SNAPSHOT") {
        exclude(group = "net.md-5", module = "bungeecord-chat")
    }
}

tasks {
    assemble {
        dependsOn("shadowJar")
    }

    shadowJar {
        archiveFileName.set("${rootProject.name}-${rootProject.version}-SPIGOT-TEST.jar")
    }

    runServer {
        minecraftVersion("1.19.2")
    }
}

tasks.withType(xyz.jpenilla.runtask.task.AbstractRun::class) {
    javaLauncher = javaToolchains.launcherFor {
        languageVersion = JavaLanguageVersion.of(17)
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
