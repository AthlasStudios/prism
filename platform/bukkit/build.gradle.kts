plugins {
    id("java")
    id("java-test-fixtures")
    id("prism-publish")
}

dependencies {
    api(project(":core"))
    "shade"(project(":reflection"))

    compileOnly("org.spigotmc:spigot-api:1.13.2-R0.1-SNAPSHOT")

    testFixturesApi("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")
    testFixturesApi("org.mockbukkit.mockbukkit:mockbukkit-v1.21:4.0.0")
}

val javaComponent = components["java"] as AdhocComponentWithVariants
javaComponent.withVariantsFromConfiguration(configurations["testFixturesApiElements"]) { skip() }
javaComponent.withVariantsFromConfiguration(configurations["testFixturesRuntimeElements"]) { skip() }