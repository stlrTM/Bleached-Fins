package stellar.ether.api.registrate.datagen.providers

import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.Registries
import net.minecraft.data.tags.IntrinsicHolderTagsProvider
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.neoforged.fml.LogicalSide
import stellar.ether.api.registrate.datagen.*
import java.util.concurrent.CompletableFuture

class ItemTags(
    override val ctx: Context<ItemTags>
) : IntrinsicHolderTagsProvider<Item>(
    ctx.output,
    Registries.ITEM,
    ctx.provider,
    { item -> item.builtInRegistryHolder().key() },
    ctx.parent.modId,
    ctx.fileHelper
), RegistrateProvider {

    override fun addTags(provider: HolderLookup.Provider) {
        ctx.genData(this)
    }

    override fun side(): LogicalSide = LogicalSide.SERVER

    override fun getName(): String = "Tags (${Providers.getNameOf(ctx.type)})"

    fun add(tag: TagKey<Item>): IntrinsicTagAppender<Item> = tag(tag)
}
