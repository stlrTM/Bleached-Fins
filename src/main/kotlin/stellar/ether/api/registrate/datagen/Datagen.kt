package stellar.ether.api.registrate.datagen

import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.neoforged.neoforge.data.event.GatherDataEvent
import stellar.ether.api.Dependencies
import stellar.ether.api.registrate.AbstractRegistrate
import stellar.ether.api.registrate.Builder
import stellar.ether.api.registrate.datagen.Providers.getNameOf

class DataCtx<R, T : R>(
    private val entryFetcher: () -> T,
    val name: String,
    val type: ResourceKey<out Registry<R>>,
    val location: ResourceLocation,
) : () -> T {

    override fun invoke(): T = entryFetcher()

    fun get(): T = entryFetcher()

    companion object {
        fun <R, T : R> from(builder: Builder<R, T, *, *>): DataCtx<R, T> {
            return DataCtx(
                entryFetcher = {
                    builder.entry?.get() ?: error("Entry not registered yet: ${builder.name}")
                }, name = builder.name, type = builder.type, location = builder.location
            )
        }
    }
}

class DataContainer(private val owner: AbstractRegistrate<*>) {
    data class Sorted(
        val id: String,
        val provider: ProviderType<*>,
        val parents: List<ProviderType<*>>,
    )

    private val generators = mutableMapOf<ProviderType<*>, MutableList<(RegistrateProvider) -> Unit>>()

    fun <T : RegistrateProvider, F, G : F> addProvider(
        builder: Builder<F, G, *, *>, type: ProviderType<T>, consumer: T.(DataCtx<F, G>) -> Unit
    ) {
        generators.getOrPut(type) { mutableListOf() }.add { prov ->
            @Suppress("UNCHECKED_CAST") (prov as T).consumer(DataCtx.from(builder))
        }
    }

    fun <T : RegistrateProvider> genData(provider: ProviderType<T>, data: T) {
        generators[provider]?.forEach { it(data) }
    }

    fun onData(event: GatherDataEvent) {
        event.addProvider(
            RegistrateData(event, owner, Dependencies.sortDependencies(generators.keys) { it.dependencies() }.map {
                Sorted(
                    getNameOf(it), it, it.dependencies()
                )
            })
        )
    }
}


