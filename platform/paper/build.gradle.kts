plugins {
    id("java")
    id("com.gradleup.shadow")
}

dependencies {
    api(project(":platform:bukkit"))
    runtimeOnly(project(":brigadier:brigadier-paper"))

    compileOnly("com.destroystokyo.paper:paper-api:1.13.2-R0.1-SNAPSHOT")
}

tasks {
    assemble {
        dependsOn("shadowJar")
    }

    shadowJar {
        archiveFileName.set("${rootProject.name}-${rootProject.version}-PAPER.jar")
    }

    register<Jar>("sourcesJar") {
        from(sourceSets.main.get().allJava)
    }
}