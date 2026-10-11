plugins {
    java
}

val nms = extensions.create<SpigotNmsExtension>("spigotNms")
val specialSource = configurations.create("specialSource")

repositories {
    mavenCentral()
    maven("https://repo.codemc.io/repository/nms/")
}

dependencies {
    addProvider("compileOnly", nms.version.map { "org.spigotmc:spigot:$it:remapped-mojang" })

    specialSource("net.md-5:SpecialSource:1.11.4:shaded")
    addProvider("specialSource", nms.version.map { "org.spigotmc:minecraft-server:$it:maps-mojang@txt" })
    addProvider("specialSource", nms.version.map { "org.spigotmc:minecraft-server:$it:maps-spigot@csrg" })
}

configurations.configureEach {
    if (isCanBeConsumed) {
        attributes.attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 8)
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

val remapperClasspath = specialSource.filter { it.name.contains("SpecialSource") }

fun mapping(extension: String) = specialSource.files.single { it.name.endsWith(".$extension") }

val obfJar = layout.buildDirectory.file("reobf/${project.name}-obf.jar")
val reobfOutput = layout.buildDirectory.file("reobf/${project.name}-spigot.jar")

val remapObf = tasks.register<JavaExec>("remapObf") {
    dependsOn(tasks.jar)

    val input = tasks.jar.flatMap { it.archiveFile }

    inputs.file(input)
    outputs.file(obfJar)

    classpath = remapperClasspath
    mainClass.set("net.md_5.specialsource.SpecialSource")

    doFirst {
        args(
            "--live",
            "-i", input.get().asFile.absolutePath,
            "-o", obfJar.get().asFile.absolutePath,
            "-m", mapping("txt").absolutePath,
            "--reverse"
        )
    }
}

val reobfJar = tasks.register<JavaExec>("reobfJar") {
    dependsOn(remapObf)

    inputs.file(obfJar)
    outputs.file(reobfOutput)

    classpath = remapperClasspath
    mainClass.set("net.md_5.specialsource.SpecialSource")

    doFirst {
        args(
            "--live",
            "-i", obfJar.get().asFile.absolutePath,
            "-o", reobfOutput.get().asFile.absolutePath,
            "-m", mapping("csrg").absolutePath
        )
    }
}

tasks.assemble {
    dependsOn(reobfJar)
}

configurations.named("runtimeElements") {
    outgoing.artifacts.clear()
    outgoing.artifact(reobfOutput) {
        builtBy(reobfJar)
    }
}