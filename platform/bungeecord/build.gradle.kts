plugins {
    id("java")
    id("prism-publish")
}

dependencies {
    api(project(":core"))
    implementation(project(":brigadier:brigadier-api"))

    compileOnly("net.md-5:bungeecord-api:1.21-R0.2")
    compileOnly("io.netty:netty-transport:4.1.118.Final")
}
