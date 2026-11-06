package stellar.ether.api.registrate.datagen.providers

import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.Registries
import net.minecraft.data.CachedOutput
import net.minecraft.data.tags.TagsProvider
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.level.biome.Biome
import net.neoforged.fml.LogicalSide
import stellar.ether.api.registrate.datagen.Context
import stellar.ether.api.registrate.datagen.Providers
import stellar.ether.api.registrate.datagen.RegistrateProvider
import java.util.concurrent.CompletableFuture

class BiomeTags(
    override val ctx: Context<BiomeTags>
) : TagsProvider<Biome>(
    ctx.output,
    Registries.BIOME,
    ctx.provider,
    ctx.parent.modId,
    ctx.fileHelper
), RegistrateProvider {

    override fun addTags(provider: HolderLookup.Provider) {
        ctx.genData(this)
    }

    override fun side(): LogicalSide = LogicalSide.SERVER

    override fun getName(): String = "Biome Tags (${Providers.getNameOf(ctx.type)})"

    fun add(key: TagKey<Biome>): TagAppender<Biome> {
        return tag(key)
    }

    fun addOptional(key: TagKey<Biome>, vararg biomes: String) {
        addOptional(key, *biomes.mapNotNull { ResourceLocation.tryParse(it) }.toTypedArray())
    }

    fun addOptional(key: TagKey<Biome>, vararg biomes: ResourceLocation) {
        val appender = tag(key)
        for (biome in biomes) {
            appender.addOptional(biome)
        }
    }


}