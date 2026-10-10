plugins {
    id("java")
}

dependencies {
    api(project(":platform:brigadier"))
    implementation(project(":reflection"))
    compileOnlyApi(project(":platform:bukkit"))

    compileOnly("org.spigotmc:spigot-api:1.13.2-R0.1-SNAPSHOT") {
        exclude(group = "net.md-5", module = "bungeecord-chat")
    }

    subprojects
        .filter { it.name.matches(Regex("V\\d+(_\\d+)*")) }
        .forEach { runtimeOnly(project(it.path)) }
}