plugins {
    id("java")
    id("prism-publish")
}

dependencies {
    api(project(":brigadier:brigadier-api"))
    implementation(project(":reflection"))
    compileOnlyApi(project(":platform:bukkit"))

    compileOnly("com.mojang:authlib:3.13.56")

    compileOnly("org.spigotmc:spigot-api:1.13.2-R0.1-SNAPSHOT") {
        exclude(group = "net.md-5", module = "bungeecord-chat")
    }

    subprojects
        .filter { it.name.matches(Regex("V\\d+(_\\d+)*")) }
        .forEach { runtimeOnly(project(it.path)) }
}

java {
    toolchain.languageVersion.set(
        JavaLanguageVersion.of(16)
    )
}

listOf("apiElements", "runtimeElements").forEach { name ->
    configurations.named(name) {
        attributes.attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 8)
    }
}