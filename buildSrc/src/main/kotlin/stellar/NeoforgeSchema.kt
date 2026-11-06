package stellar

import java.io.Serializable
import java.util.SortedMap

data class ModsToml(
    val modLoader: String,
    val loaderVersion: String,
    val license: String,
    val showAsResourcePack: Boolean,
    val showAsDataPack: Boolean,
    val services: List<String>,
    val properties: SortedMap<String, String>,
    val issueTrackerURL: String?,
    val mods: List<ModEntry>,
    val dependencies: SortedMap<String, List<Dependency>>,
    val features: SortedMap<String, List<Feature>>,
    val modProperties: SortedMap<String, SortedMap<String, String>>,
    val accessTransformers: List<AccessTransformer>,
    val mixins: List<MixinConfig>,

    ) : Serializable {

    companion object {
        operator fun invoke(
            modLoader: String = "javafml",
            loaderVersion: String,
            license: String,
            showAsResourcePack: Boolean = false,
            showAsDataPack: Boolean = false,
            services: List<String> = emptyList(),
            properties: Map<String, String> = emptyMap(),
            issueTrackerURL: String? = null,
            mods: List<ModEntry>,
            dependencies: Map<String, List<Dependency>> = emptyMap(),
            features: Map<String, List<Feature>> = emptyMap(),
            modProperties: Map<String, Map<String, String>> = emptyMap(),
            accessTransformers: List<AccessTransformer> = emptyList(),
            mixins: List<MixinConfig> = emptyList(),
        ): ModsToml = ModsToml(
            modLoader = modLoader,
            loaderVersion = loaderVersion,
            license = license,
            showAsResourcePack = showAsResourcePack,
            showAsDataPack = showAsDataPack,
            services = services,
            properties = properties.toSortedMap(),
            issueTrackerURL = issueTrackerURL,
            mods = mods,
            dependencies = dependencies.toSortedMap(),
            features = features.toSortedMap(),
            modProperties = modProperties.mapValues { it.value.toSortedMap() }.toSortedMap(),
            accessTransformers = accessTransformers,
            mixins = mixins,
        )
    }
}

data class ModEntry(
    val modId: String,
    val namespace: String? = null,
    val version: String = "1",
    val displayName: String? = null,
    val description: String = "MISSING DESCRIPTION",
    val logoFile: String? = null,
    val logoBlur: Boolean = true,
    val updateJSONURL: String? = null,
    val modUrl: String? = null,
    val credits: String? = null,
    val authors: String? = null,
    val displayURL: String? = null,
    val enumExtensions: String? = null,
) : Serializable

data class Dependency(
    val modId: String,
    val type: DependencyType = DependencyType.REQUIRED,
    val reason: String? = null,
    val versionRange: String = "",
    val ordering: Ordering = Ordering.NONE,
    val side: Side = Side.BOTH,
    val referralUrl: String? = null,
) : Serializable

enum class DependencyType {
    REQUIRED, OPTIONAL, INCOMPATIBLE, DISCOURAGED;

    fun tomlValue(): String = name.lowercase()
}

enum class Ordering {
    NONE, BEFORE, AFTER;

    fun tomlValue(): String = name
}

enum class Side {
    BOTH, CLIENT, SERVER;

    fun tomlValue(): String = name
}

data class Feature(
    val javaVersion: String? = null,
    val openGLVersion: String? = null,
) : Serializable

data class AccessTransformer(
    val file: String,
) : Serializable

data class MixinConfig(
    val config: String,
) : Serializable