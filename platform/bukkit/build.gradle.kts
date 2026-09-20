    plugins {
        id("java")
        id("java-test-fixtures")
    }

    repositories {
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
    }

    dependencies {
        api(project(":core"))

        api("org.spigotmc:spigot-api:1.13.2-R0.1-SNAPSHOT") {
            exclude(group = "net.md-5", module = "bungeecord-chat")
        }

        testFixturesImplementation("com.github.seeseemelk:MockBukkit-v1.13:0.2.0")
    }