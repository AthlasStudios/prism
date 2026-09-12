plugins {
    id("com.gradleup.shadow")
    id("xyz.jpenilla.run-paper")
    id("com.modrinth.minotaur")

    id("xyz.jpenilla.resource-factory-bukkit-convention")
    id("xyz.jpenilla.resource-factory-paper-convention")

    // uncomment to add paper-weight
    // id("io.papermc.paperweight.userdev")
}

data class PluginData(
    val mainClass: String,
    val apiVersion: String,
    val description: String,
    val authors: List<String>,
    val foliaSupported: Boolean
)

val pluginData = PluginData(
    mainClass = "club.athlas.athlasdevtemplate.AthlasDevTemplate",
    apiVersion = "1.21",
    description = "Athlas Development Plugin Template",
    authors = listOf("Athlas Development Team"),
    foliaSupported = false
)

dependencies {
    implementation(project(":api"))

    // your dependencies

    // Uncomment to add paper-weight bundle
    // paperweight.paperDevBundle("1.21.5-R0.1-SNAPSHOT")
}

tasks {
    assemble {
        dependsOn(shadowJar)
    }

    shadowJar {
        archiveFileName = "${rootProject.name}-${rootProject.version}.jar"

        // relocations
    }

    runServer {
        minecraftVersion("1.21.5")
    }

    @Suppress("UnstableApiUsage")
    withType(xyz.jpenilla.runtask.task.AbstractRun::class) {
        javaLauncher = rootProject.javaToolchains.launcherFor {
            vendor = JvmVendorSpec.JETBRAINS
            languageVersion = JavaLanguageVersion.of(21)
        }

        jvmArgs("-XX:+AllowEnhancedClassRedefinition")
    }
}

bukkitPluginYaml {
    main.set(pluginData.mainClass)
    apiVersion.set(pluginData.apiVersion)

    name.set(rootProject.name)
    version.set(rootProject.version.toString())
    description.set(pluginData.description)
    authors.set(pluginData.authors)

    foliaSupported.set(pluginData.foliaSupported)
}

paperPluginYaml {
    main.set(pluginData.mainClass)
    apiVersion.set(pluginData.apiVersion)

    name.set(rootProject.name)
    version.set(rootProject.version.toString())
    description.set(pluginData.description)
    authors.set(pluginData.authors)

    foliaSupported.set(pluginData.foliaSupported)
}

modrinth {
    token.set(System.getenv("MODRINTH_TOKEN"))
    projectId.set(rootProject.name.lowercase())
    versionNumber.set(version.toString())
    versionType.set("beta") // release, beta, alpha
    uploadFile.set(file("build/libs/${rootProject.name}-${rootProject.version}.jar"))
    gameVersions.addAll(
        "1.21.5", "1.21.4", "1.21.3", "1.21.2", "1.21.1", "1.21",
        "1.20.6", "1.20.5", "1.20.4", "1.20.3", "1.20.2", "1.20.1", "1.20",
        "1.19.4", "1.19.3", "1.19.2", "1.19.1", "1.19",
        "1.18.2", "1.18.1", "1.18",
        "1.17.1", "1.17"
    )
    loaders.addAll("bukkit", "spigot", "paper", "purpur")
}