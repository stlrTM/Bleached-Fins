package stellar.ether.api.registrate.builders

import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import stellar.ether.api.registrate.AbstractBuilder
import stellar.ether.api.registrate.AbstractRegistrate
import stellar.ether.api.registrate.RDSL

abstract class BuilderWithSettings<E, R, T : R, P, S : AbstractBuilder<R, T, P, S>>(
    owner: AbstractRegistrate<*>,
    parent: P,
    name: String,
    type: ResourceKey<out Registry<R>>,
    var initialProperties: () -> E,
    val propertiesToEntry: (E) -> T
) : AbstractBuilder<R, T, P, S>(
    owner,
    parent,
    name,
    type
) {
    private var properties: (E) -> E = { it }

    fun initialProperties(initial: @RDSL () -> E) {
        initialProperties = initial
    }

    fun properties(action: @RDSL E.() -> Unit) {
        properties = {
            action(it)
            it
        }
    }

    override fun createEntry(): T = propertiesToEntry(properties(initialProperties()))
}