package stellar.ether.api.registrate.builders

import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.*
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent
import net.neoforged.neoforge.registries.DeferredHolder
import stellar.ether.api.registrate.AbstractRegistrate
import stellar.ether.api.registrate.LazyEntry
import stellar.ether.api.registrate.RDSL
import stellar.ether.api.registrate.datagen.DataCtx

class ItemBuilder<T : Item,P>(
    owner: AbstractRegistrate<*>,
    parent: P,
    name: String,
    factory: (Item.Properties) -> T
) : BuilderWithSettings<Item.Properties, Item, T, P, ItemBuilder<T, P>>(
    owner,
    parent,
    name,
    Registries.ITEM,
    { Item.Properties() },
    factory
) {
    private val tabs: MutableMap<ResourceKey<CreativeModeTab>,@RDSL BuildCreativeModeTabContentsEvent.(DataCtx<Item, T>) -> Unit> = mutableMapOf()

    init {
        addEventListener(
            BuildCreativeModeTabContentsEvent::class.java
        ) {
            tabs.forEach { (key, value) ->
                if(tabKey == key) {
                    value(this,DataCtx.from(this@ItemBuilder))
                }
            }
        }
    }

    fun tab(key: ResourceKey<CreativeModeTab>,action: @RDSL BuildCreativeModeTabContentsEvent.(DataCtx<Item,T>) -> Unit) {
        tabs[key] = action
    }

    fun tab(key: ResourceKey<CreativeModeTab>) = tab(key) {
        accept(this@ItemBuilder.entry!!.get())
    }

    fun tab(key: LazyEntry<CreativeModeTab, CreativeModeTab>) = tab(key.key!!)

    override fun createEntryWrapper(holder: DeferredHolder<Item, T>): ItemEntry<T> {
        return ItemEntry(owner,holder.key!!) as ItemEntry<T>
    }
}

object ItemBuilders {
    fun <T : Item,S : AbstractRegistrate<S>> AbstractRegistrate<S>.item(
        name: String,
        factory: (Item.Properties) -> T,
        action: ItemBuilder<T, AbstractRegistrate<S>>.() -> Unit
    ): ItemEntry<T> = item(
        name,
        factory,
        this,
        action
    )

    fun <T : Item,P> AbstractRegistrate<*>.item(
        name: String,
        factory: (Item.Properties) -> T,
        parent: P,
        action: ItemBuilder<T, P>.() -> Unit
    ): ItemEntry<T> = action(ItemBuilder(this,parent,name,factory),action) as ItemEntry<T>
}

class ItemEntry<T : Item>(
    owner: AbstractRegistrate<*>,
    key: ResourceKey<T>,
) : ItemLikeEntry<Item, T>(
    owner,
    key as ResourceKey<Item>
)