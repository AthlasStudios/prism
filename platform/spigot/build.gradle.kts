plugins {
    id("maven-publish")
    id("com.gradleup.shadow")
}

dependencies {
    api(project(":platform:bukkit"))
    implementation(project(":platform:brigadier:minecraft"))
    implementation(project(":reflection"))

    compileOnly("org.spigotmc:spigot-api:1.13.2-R0.1-SNAPSHOT") {
        exclude(group = "net.md-5", module = "bungeecord-chat")
    }

    testImplementation(testFixtures(project(":platform:bukkit")))
}

tasks {
    assemble {
        dependsOn("shadowJar")
    }

    shadowJar {
        archiveFileName.set("${rootProject.name}-${rootProject.version}-SPIGOT.jar")
    }

    register<Jar>("sourcesJar") {
        from(sourceSets.main.get().allJava)
    }
}