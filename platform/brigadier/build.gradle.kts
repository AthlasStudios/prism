plugins {
    id("java")
}

dependencies {
    api(project(":core"))
    api(project(":platform:bukkit"))

    compileOnly("com.mojang:brigadier:1.0.18")

    testImplementation(testFixtures(project(":platform:bukkit")))
}