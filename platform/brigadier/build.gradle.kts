plugins {
    id("java")
}

dependencies {
    compileOnlyApi(project(":core"))
    compileOnlyApi("com.mojang:brigadier:1.0.18")
}