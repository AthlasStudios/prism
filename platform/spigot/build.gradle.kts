plugins {
    id("maven-publish")
    id("com.gradleup.shadow")
}

dependencies {
    api(project(":core"))
    api(project(":platform:bukkit"))
    implementation(project(":reflection"))
    implementation(project(":platform:brigadier"))

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