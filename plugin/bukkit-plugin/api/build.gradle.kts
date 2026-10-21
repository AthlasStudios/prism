plugins {
    id("com.gradleup.shadow")
}

repositories {
    mavenCentral()
}

dependencies {

}

tasks {
    assemble {
        dependsOn("shadowJar")
    }

    shadowJar {
        archiveFileName.set("${rootProject.name}-${rootProject.version}-API.jar")
    }

    register<Jar>("sourcesJar") {
        from(sourceSets.main.get().allJava)
    }
}