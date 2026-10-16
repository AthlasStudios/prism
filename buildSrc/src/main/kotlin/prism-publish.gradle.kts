import com.vanniktech.maven.publish.JavaLibrary
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import org.gradle.api.artifacts.component.ProjectComponentIdentifier
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication

plugins {
    id("java-library")
    id("com.vanniktech.maven.publish")
}

val aggregator = path.startsWith(":publish:")
val prismArtifactId = "prism-" + project.name.removeSuffix("-bundle")

val shade = configurations.create("shade") {
    isCanBeConsumed = false
    isCanBeResolved = true
    attributes {
        attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME))
        attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.LIBRARY))
        attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, objects.named(LibraryElements.JAR))

        attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.EXTERNAL))
        attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 25)
    }
}

configurations.compileOnly { extendsFrom(shade) }
configurations.testImplementation { extendsFrom(shade) }

val shaded = shade.incoming.artifactView {
    componentFilter { it is ProjectComponentIdentifier }
}.files

tasks.jar {
    dependsOn(shaded)
    from(shaded.elements.map { files -> files.map { zipTree(it.asFile) } })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")
}

tasks.withType<Javadoc>().configureEach {
    (options as StandardJavadocDocletOptions).addStringOption("Xdoclint:none", "-quiet")
    isFailOnError = false
}

// The bundles have no sources of their own (they only repackage other modules), so they get a
// single, explicit sources jar instead of the one the publish plugin would generate.
if (aggregator) {
    val bundleSourcesJar = tasks.register<Jar>("bundleSourcesJar") {
        archiveClassifier.set("sources")
    }

    extensions.configure<PublishingExtension> {
        publications.withType<MavenPublication>().configureEach {
            artifact(bundleSourcesJar)
        }
    }
}

extensions.configure<MavenPublishBaseExtension> {
    publishToMavenCentral()
    signAllPublications()
    coordinates(project.group.toString(), prismArtifactId, project.version.toString())
    configure(
        JavaLibrary(
            javadocJar = if (aggregator) JavadocJar.Empty() else JavadocJar.Javadoc(),
            sourcesJar = !aggregator
        )
    )
    pom {
        name.set(prismArtifactId)
        description.set("Prism command library ($prismArtifactId)")
        inceptionYear.set("2026")
        url.set("https://github.com/AthlasStudios/prism")
        licenses {
            license {
                name.set("MIT License")
                url.set("https://opensource.org/license/mit")
            }
        }
        developers {
            developer {
                id.set("gurwi")
                name.set("Gurwi")
                url.set("https://gurwi.dev")
            }
        }
        scm {
            url.set("https://github.com/AthlasStudios/prism")
            connection.set("scm:git:git://github.com/AthlasStudios/prism.git")
            developerConnection.set("scm:git:ssh://git@github.com/AthlasStudios/prism.git")
        }
    }
}