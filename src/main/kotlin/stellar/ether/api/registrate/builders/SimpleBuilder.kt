package stellar.ether.api.registrate.builders

import net.minecraft.core.Registry
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvent
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import stellar.ether.api.registrate.AbstractBuilder
import stellar.ether.api.registrate.AbstractRegistrate
import stellar.ether.api.registrate.LazyEntry

class SimpleBuilder<T,P>(
    owner: AbstractRegistrate<*>,
    parent: P,
    name: String,
    type: ResourceKey<out Registry<T>>,
    val factory: () -> T
) : AbstractBuilder<T, T, P, SimpleBuilder<T, P>>(
    owner,
    parent,
    name,
    type
) {
    override fun createEntry(): T = factory()
}

object SimpleBuilders {
    fun <S : AbstractRegistrate<S>> AbstractRegistrate<S>.soundEvent(
        name: String,
        factory: (ResourceLocation) -> SoundEvent = SoundEvent::createVariableRangeEvent
    ): LazyEntry<SoundEvent, SoundEvent> = simple(
        name,
        {
            factory(ResourceLocation.fromNamespaceAndPath(this.modId,name))
        },
        Registries.SOUND_EVENT
    ) {

    }
    fun <T,S : AbstractRegistrate<S>> AbstractRegistrate<S>.simple(
        name: String,
        factory: () -> T,
        type: ResourceKey<out Registry<T>>,
        action: SimpleBuilder<T, AbstractRegistrate<S>>.() -> Unit
    ): LazyEntry<T, T> = simple(
        name,
        factory,
        type,
        this,
        action
    )

    fun <T,P> AbstractRegistrate<*>.simple(
        name: String,
        factory: () -> T,
        type: ResourceKey<out Registry<T>>,
        parent: P,
        action: SimpleBuilder<T, P>.() -> Unit
    ): LazyEntry<T, T> = action(SimpleBuilder(this,parent,name,type,factory),action)

    fun AbstractRegistrate<*>.creativeModeTab(
        name: String,
        builder: CreativeModeTab.Builder.() -> Unit = {},
        action: CreativeModeTab.() -> Unit = {}
    ): LazyEntry<CreativeModeTab, CreativeModeTab> = creativeModeTab(
        name,
        this,
        builder,
        action
    )

    fun <P> AbstractRegistrate<*>.creativeModeTab(
        name: String,
        parent: P,
        builder: CreativeModeTab.Builder.() -> Unit = {},
        action: CreativeModeTab.() -> Unit = {}
    ): LazyEntry<CreativeModeTab, CreativeModeTab> {
        val built = SimpleBuilder(this,parent,name, Registries.CREATIVE_MODE_TAB) {
            val tab = CreativeModeTab.builder()
            tab.icon { (this.registrations.first { entry -> entry.key.type == Registries.ITEM }.entry as LazyEntry<Item, Item>).get().defaultInstance }
            builder(tab)
            val realTab = tab.build()
            action(realTab)
            return@SimpleBuilder realTab
        }
        return built.register()
    }
}