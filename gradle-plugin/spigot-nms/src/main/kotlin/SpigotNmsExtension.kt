import org.gradle.api.provider.Property

abstract class SpigotNmsExtension {
    abstract val version: Property<String>
}