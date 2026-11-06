package stellar.ether.api.registrate

import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.neoforged.neoforge.registries.DeferredHolder

/**
 * Abstract builder that all builders should extend
 *
 * @param R the registry type
 * @param T the current building type,always a subclass of [R]
 * @param P the parent type,usually [AbstractRegistrate]
 * @param S the self type,a recursive type for support of building nested patterns
 * @property owner the owner registrate
 * @property parent the parent of this builder
 * @property name the name of this builder
 * @property type the resource key for [R]
 */
abstract class AbstractBuilder<R, T : R, P, S : AbstractBuilder<R, T, P, S>>(
    override val owner: AbstractRegistrate<*>,
    override val parent: P,
    override val name: String,
    override val type: ResourceKey<out Registry<R>>
) : Builder<R, T, P, S> {
    private val listeners: MutableList<(T) -> Unit> = mutableListOf()

    /**
     * Function that builders should override to create an entry,like an item instance.
     *
     * @return the built entry
     */
    protected abstract fun createEntry(): T

    fun onRegister(action: (T) -> Unit) {
        listeners.add(action)
    }

    fun create(): T {
        val value = createEntry()
        for(listener in listeners) {
            listener(value)
        }
        return value
    }

    /**
     * Creates a new entry wrapper,builders can override this to create like an ItemEntry wrapper
     *
     * @param holder the holder with the key for this entry to get created
     * @return the entry
     */
    protected open fun createEntryWrapper(holder: DeferredHolder<R, T>): LazyEntry<R, T> =
        LazyEntry(owner, holder.key !!)

    override fun register(): LazyEntry<R, T> = owner.accept(this, name, type, ::create, ::createEntryWrapper)

}