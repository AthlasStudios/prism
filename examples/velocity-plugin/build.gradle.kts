plugins {
    id("java")
    id("com.gradleup.shadow")
    id("xyz.jpenilla.run-velocity") version "3.1.0"
}

dependencies {
    implementation(project(":platform:velocity"))

    compileOnly("com.velocitypowered:velocity-api:4.2.1-SNAPSHOT")
    annotationProcessor("com.velocitypowered:velocity-api:4.2.1-SNAPSHOT")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}

tasks {
    assemble {
        dependsOn("shadowJar")
    }

    withType<JavaCompile> {
        options.release.set(25)
    }

    test {
        javaLauncher.set(
            project.javaToolchains.launcherFor {
                languageVersion.set(JavaLanguageVersion.of(25))
            }
        )
    }

    shadowJar {
        archiveFileName.set("${rootProject.name}-${rootProject.version}-VELOCITY-EXAMPLE.jar")
    }

    runVelocity {
        velocityVersion("4.3.0")
    }
}

tasks.withType(xyz.jpenilla.runtask.task.AbstractRun::class) {
    javaLauncher = javaToolchains.launcherFor {
        languageVersion = JavaLanguageVersion.of(25)
    }
}