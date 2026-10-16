import com.vanniktech.maven.publish.JavaLibrary
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import org.gradle.api.artifacts.component.ProjectComponentIdentifier

plugins {
    id("java-library")
    id("com.vanniktech.maven.publish")
}

val aggregator = path.startsWith(":publish:")
val prismArtifactId = "prism-${project.name}"

val publishGroup = rootProject.group.toString()

if (aggregator) {
    group = "$publishGroup.bundle"
}

val shade = configurations.create("shade") {
    isCanBeConsumed = false
    isCanBeResolved = true
    attributes {
        attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME))
        attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.LIBRARY))
        attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, objects.named(LibraryElements.JAR))
        // Same as every normal Java consumer. Without it, projects that expose extra variants
        // (paperweight's "reobf", shadow's "shadowRuntimeElements") are ambiguous.
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

extensions.configure<MavenPublishBaseExtension> {
    publishToMavenCentral()
    signAllPublications()
    coordinates(publishGroup, prismArtifactId, project.version.toString())
    configure(
        JavaLibrary(
            javadocJar = if (aggregator) JavadocJar.Empty() else JavadocJar.Javadoc(),
            sourcesJar = true
        )
    )
    pom {
        name.set(prismArtifactId)
        description.set("Prism command library (${project.name})")
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