package stellar.ether.api.registrate.datagen.providers

import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.Registries
import net.minecraft.data.tags.TagsProvider
import net.minecraft.tags.TagKey
import net.minecraft.world.level.block.Block
import net.neoforged.fml.LogicalSide
import stellar.ether.api.registrate.datagen.Context
import stellar.ether.api.registrate.datagen.Providers
import stellar.ether.api.registrate.datagen.RegistrateFilledLookupProvider
import stellar.ether.api.registrate.datagen.RegistrateProvider
import java.util.concurrent.CompletableFuture

class Tags(
    override val ctx: Context<Tags>
) : TagsProvider<Block>(
    ctx.output,
    Registries.BLOCK,
    ctx.provider,
    ctx.parent.modId,
    ctx.fileHelper
), RegistrateProvider {

    override fun addTags(provider: HolderLookup.Provider) {
        ctx.genData(this)
    }

    override fun side(): LogicalSide = LogicalSide.SERVER

    override fun getName(): String = "Tags (${Providers.getNameOf(ctx.type)})"

    fun add(key: TagKey<Block>): TagAppender<Block> {
        return tag(key)
    }
}
