package stellar.ether.api.registrate

import net.minecraft.resources.ResourceKey
import net.neoforged.neoforge.registries.DeferredHolder
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

open class LazyEntry<R, T : R>(
    val owner: AbstractRegistrate<*>,
    key: ResourceKey<R>
) : DeferredHolder<R, T>(key), ReadOnlyProperty<Any?, T>, () -> T {
    override fun getValue(thisRef: Any?, property: KProperty<*>): T = get()

    fun asSupplier(): () -> T = ::get
    override fun invoke(): T = get()
}