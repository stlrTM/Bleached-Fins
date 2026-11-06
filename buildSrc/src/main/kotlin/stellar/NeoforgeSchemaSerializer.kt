package stellar

import com.electronwill.nightconfig.core.Config
import com.electronwill.nightconfig.toml.TomlFormat
import java.io.File
import java.io.StringWriter

object ModsTomlSerializer {

    fun serialize(toml: ModsToml): String {
        val root = TomlFormat.newConfig()
        write(root, toml)
        val sw = StringWriter()
        TomlFormat.instance().createWriter().write(root, sw)
        return sw.toString()
    }

    fun writeTo(toml: ModsToml, file: File) {
        file.parentFile?.mkdirs()
        file.writeText(serialize(toml))
    }

    private fun write(root: Config, toml: ModsToml) {
        root.set<Any>("modLoader", toml.modLoader)
        root.set<Any>("loaderVersion", toml.loaderVersion)
        root.set<Any>("license", toml.license)

        if (toml.showAsResourcePack) root.set<Any>("showAsResourcePack", true)
        if (toml.showAsDataPack) root.set<Any>("showAsDataPack", true)
        if (toml.services.isNotEmpty()) root.set<Any>("services", toml.services)

        toml.properties.forEach { (key, value) ->
            root.set<Any>(listOf("properties", key), value)
        }

        toml.issueTrackerURL?.let { root.set<Any>("issueTrackerURL", it) }

        root.set<Any>("mods", toml.mods.map(::modConfig))

        toml.dependencies.forEach { (modId, deps) ->
            root.set<Any>(listOf("dependencies", modId), deps.map(::dependencyConfig))
        }

        toml.features.forEach { (modId, feats) ->
            root.set<Any>(listOf("features", modId), feats.map(::featureConfig))
        }

        toml.modProperties.forEach { (modId, props) ->
            val propConfig = TomlFormat.newConfig()
            props.forEach { (k, v) -> propConfig.set<Any>(listOf(k), v) }
            root.set<Any>(listOf("modproperties", modId), listOf(propConfig))
        }

        if (toml.accessTransformers.isNotEmpty()) {
            root.set<Any>("accessTransformers", toml.accessTransformers.map {
                TomlFormat.newConfig().apply { set<Any>("file", it.file) }
            })
        }

        if (toml.mixins.isNotEmpty()) {
            root.set<Any>("mixins", toml.mixins.map {
                TomlFormat.newConfig().apply { set<Any>("config", it.config) }
            })
        }
    }

    private fun modConfig(mod: ModEntry): Config {
        val c = TomlFormat.newConfig()
        c.set<Any>("modId", mod.modId)
        mod.namespace?.let { c.set<Any>("namespace", it) }
        c.set<Any>("version", mod.version)
        mod.displayName?.let { c.set<Any>("displayName", it) }
        c.set<Any>("description", mod.description)
        mod.logoFile?.let { c.set<Any>("logoFile", it) }
        if (!mod.logoBlur) c.set<Any>("logoBlur", false)
        mod.updateJSONURL?.let { c.set<Any>("updateJSONURL", it) }
        mod.modUrl?.let { c.set<Any>("modUrl", it) }
        mod.credits?.let { c.set<Any>("credits", it) }
        mod.authors?.let { c.set<Any>("authors", it) }
        mod.displayURL?.let { c.set<Any>("displayURL", it) }
        mod.enumExtensions?.let { c.set<Any>("enumExtensions", it) }
        return c
    }

    private fun dependencyConfig(dep: Dependency): Config {
        val c = TomlFormat.newConfig()
        c.set<Any>("modId", dep.modId)
        c.set<Any>("type", dep.type.tomlValue())
        dep.reason?.let { c.set<Any>("reason", it) }
        if (dep.versionRange.isNotEmpty()) c.set<Any>("versionRange", dep.versionRange)
        if (dep.ordering != Ordering.NONE) c.set<Any>("ordering", dep.ordering.tomlValue())
        if (dep.side != Side.BOTH) c.set<Any>("side", dep.side.tomlValue())
        dep.referralUrl?.let { c.set<Any>("referralUrl", it) }
        return c
    }

    private fun featureConfig(feature: Feature): Config {
        val c = TomlFormat.newConfig()
        feature.javaVersion?.let { c.set<Any>("javaVersion", it) }
        feature.openGLVersion?.let { c.set<Any>("openGLVersion", it) }
        return c
    }
}