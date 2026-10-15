import org.gradle.api.provider.Property

enum class NmsMappings {
    NONE,
    MOJANG
}

abstract class SpigotNmsExtension {
    abstract val version: Property<String>
    abstract val mappings: Property<NmsMappings>
}