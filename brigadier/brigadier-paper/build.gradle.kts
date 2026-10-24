plugins {
    id("java")
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.24"
}

dependencies {
    api(project(":brigadier:brigadier-minecraft"))
    implementation(project(":reflection"))
    paperweight.paperDevBundle("26.3.build.+")
}

configurations.configureEach {
    if (isCanBeConsumed) {
        attributes.attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 8)
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(25)
}

tasks.test {
    enabled = false
}