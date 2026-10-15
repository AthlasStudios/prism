import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.provider.Property

enum class NMSMapping {
    SPIGOT,
    MOJANG
}

abstract class SpigotNmsExtension {
    abstract val version: Property<String>
    abstract val mappings: Property<NMSMapping>
}

fun DependencyHandler.spigotNms(version: String, mappings: NMSMapping = NMSMapping.SPIGOT) {
    val nms = extensions.getByType(SpigotNmsExtension::class.java)

    nms.version.set(version)
    nms.mappings.set(mappings)
}