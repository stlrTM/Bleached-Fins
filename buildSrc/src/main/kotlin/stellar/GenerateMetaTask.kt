package stellar

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*

@CacheableTask
abstract class GenerateMetaTask : DefaultTask() {
    @get:Input
    abstract val meta: Property<ModsToml>

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun execute() {
        val baseDir = outputDir.get().asFile
        val targetFile = baseDir.resolve("META-INF/neoforge.mods.toml")
        targetFile.parentFile.mkdirs()
        ModsTomlSerializer.writeTo(meta.get(), targetFile)
    }
}