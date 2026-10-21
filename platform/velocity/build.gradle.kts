plugins {
    id("java")
//    id("prism-publish") // NOT YET
}

dependencies {
    api(project(":core"))
    implementation(project(":brigadier:brigadier-api"))

    compileOnly("com.velocitypowered:velocity-api:4.2.1-SNAPSHOT")
    annotationProcessor("com.velocitypowered:velocity-api:4.2.1-SNAPSHOT")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}
