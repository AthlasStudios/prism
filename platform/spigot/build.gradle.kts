plugins {
    id("maven-publish")
    id("com.gradleup.shadow")
}

repositories {
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    api(project(":core"))
    implementation(project(":reflection"))
    implementation(project(":platform:brigadier"))

    compileOnly("org.spigotmc:spigot-api:1.13.2-R0.1-SNAPSHOT") {
        exclude(group = "net.md-5", module = "bungeecord-chat")
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