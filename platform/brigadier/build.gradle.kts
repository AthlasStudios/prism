plugins {
    id("java")
}

repositories {
    maven("https://libraries.minecraft.net")
}

dependencies {
    implementation(project(":core"))
    compileOnly("com.mojang:brigadier:1.0.18")

    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}