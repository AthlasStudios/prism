plugins {
    id("java")
}

dependencies {
    api(project(":core"))

    compileOnlyApi("com.mojang:brigadier:1.0.18")
}