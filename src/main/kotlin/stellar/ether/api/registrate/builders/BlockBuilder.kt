package stellar.ether.api.registrate.builders

import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
import net.neoforged.neoforge.registries.DeferredHolder
import stellar.ether.api.registrate.AbstractRegistrate
import stellar.ether.api.registrate.RDSL
import stellar.ether.api.registrate.builders.ItemBuilders.item

class BlockBuilder<T : Block, P>(
    owner: AbstractRegistrate<*>,
    parent: P,
    name: String,
    factory: (BlockBehaviour.Properties) -> T
) : BuilderWithSettings<BlockBehaviour.Properties, Block, T, P, BlockBuilder<T, P>>(
    owner,
    parent,
    name,
    Registries.BLOCK,
    { BlockBehaviour.Properties.of() },
    factory
) {

    fun <I : Item> item(
        factory: (Block, Item.Properties) -> I,
        action: @RDSL ItemBuilder<I, BlockBuilder<T, P>>.() -> Unit = {}
    ): BlockBuilder<T, P> {
        owner.item(
            name = name,
            factory = { props -> factory(this.entry!!.get(), props) },
            parent = this,
            action = action
        )
        return this
    }

    fun item(action: @RDSL ItemBuilder<BlockItem, BlockBuilder<T, P>>.() -> Unit = {}): BlockBuilder<T, P> {
        return item(::BlockItem, action)
    }

    override fun createEntryWrapper(holder: DeferredHolder<Block, T>): BlockEntry<T> {
        return BlockEntry(owner, holder.key!!) as BlockEntry<T>
    }
}

object BlockBuilders {

    fun <T : Block, S : AbstractRegistrate<S>> AbstractRegistrate<S>.block(
        name: String,
        factory: (BlockBehaviour.Properties) -> T,
        action: BlockBuilder<T, AbstractRegistrate<S>>.() -> Unit = {}
    ): BlockEntry<T> = block(
        name,
        factory,
        this,
        action
    )

    fun <T : Block, P> AbstractRegistrate<*>.block(
        name: String,
        factory: (BlockBehaviour.Properties) -> T,
        parent: P,
        action: BlockBuilder<T, P>.() -> Unit = {}
    ): BlockEntry<T> = action(BlockBuilder(this, parent, name, factory), action) as BlockEntry<T>
}

class BlockEntry<T : Block>(
    owner: AbstractRegistrate<*>,
    key: ResourceKey<T>,
) : ItemLikeEntry<Block, T>(
    owner,
    key as ResourceKey<Block>
)