package stellar.ether.api.registrate.datagen

import com.google.common.collect.HashBiMap
import stellar.ether.api.registrate.datagen.providers.BiomeTags
import stellar.ether.api.registrate.datagen.providers.BlockModels
import stellar.ether.api.registrate.datagen.providers.Blockstates
import stellar.ether.api.registrate.datagen.providers.DataMaps
import stellar.ether.api.registrate.datagen.providers.Datapack
import stellar.ether.api.registrate.datagen.providers.ItemModels
import stellar.ether.api.registrate.datagen.providers.Lang
import stellar.ether.api.registrate.datagen.providers.Loot
import stellar.ether.api.registrate.datagen.providers.Recipes
import stellar.ether.api.registrate.datagen.providers.Tags
import stellar.ether.api.registrate.datagen.providers.ItemTags

object Providers {
    val TYPES: HashBiMap<String, ProviderType<*>> = HashBiMap.create<String, ProviderType<*>>()

    fun <T : RegistrateProvider> create(
        factory: (ctx: Context<T>) -> T,
        dependencies: List<ProviderType<*>> = emptyList(),
    ): ProviderType<T> = object : ProviderType<T> {
        override fun create(ctx: Context<T>): T = factory(ctx)
        override fun dependencies(): List<ProviderType<*>> = dependencies
    }

    fun <T : RegistrateProvider> register(name: String, provider: ProviderType<T>): ProviderType<T> {
        TYPES.put(name, provider)
        return provider
    }

    @JvmStatic
    fun getNameOf(type: ProviderType<*>): String = TYPES.inverse()[type]!!

    val LANG = register("lang",create(::Lang))
    val BLOCK_TAGS = register("block_tags",create(::Tags))
    val ITEM_TAGS = register("item_tags",create(::ItemTags))
    val BIOME_TAGS = register("biome_tags",create(::BiomeTags))
    val ITEM_MODELS = register("item_models",create(::ItemModels))
    val BLOCK_MODELS = register("block_models",create(::BlockModels))
    val BLOCKSTATE = register("blockstate",create(::Blockstates))
    val DATAMAPS = register("datamaps",create(::DataMaps))
    val LOOT = register("loot",create(::Loot))
    val RECIPE = register("recipe",create(::Recipes))
    val DATAPACK = register("datapack",create(::Datapack))
}