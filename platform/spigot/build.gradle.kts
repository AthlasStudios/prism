plugins {
    id("maven-publish")
    id("com.gradleup.shadow")
}

dependencies {
    api(project(":platform:bukkit"))
    implementation(project(":reflection"))
    "runtimeOnly"(project(":brigadier:brigadier-minecraft"))

    compileOnly("org.spigotmc:spigot-api:1.13.2-R0.1-SNAPSHOT") {
        exclude(group = "net.md-5", module = "bungeecord-chat")
    }

    testImplementation(testFixtures(project(":platform:bukkit")))
}

configurations.runtimeClasspath {
    attributes {
        attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 16)
    }
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