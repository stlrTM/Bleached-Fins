package stellar.ether.api.registrate.datagen

import net.minecraft.core.HolderLookup
import net.minecraft.data.CachedOutput
import net.minecraft.data.DataProvider
import net.neoforged.fml.LogicalSide
import net.neoforged.neoforge.data.event.GatherDataEvent
import stellar.ether.api.registrate.AbstractRegistrate
import java.util.EnumSet
import java.util.concurrent.CompletableFuture

// это очень страшно но это уже не моя проблема это работает и мне все равно
data class RegistrateData(
    val event: GatherDataEvent,
    val parent: AbstractRegistrate<*>,
    val sorted: List<DataContainer.Sorted>,
) : DataProvider {
    val registries: CompletableFuture<HolderLookup.Provider> = event.lookupProvider
    val providers: MutableMap<ProviderType<*>, RegistrateProvider> = mutableMapOf()

    init {

        val sides = EnumSet.noneOf(LogicalSide::class.java)
        if (event.includeServer()) sides.add(LogicalSide.SERVER)
        if (event.includeClient()) sides.add(LogicalSide.CLIENT)

        val known: MutableMap<ProviderType<*>, RegistrateProvider> = mutableMapOf()
        for (sorted in sorted) {
            val type = sorted.provider
            var lookup = registries

            for (filler in sorted.parents) {
                val parent = known[filler]
                if (parent !is RegistrateFilledLookupProvider) error("Not a filler provider! $sorted")
                lookup = parent.lookup()
            }

            @Suppress("UNCHECKED_CAST") val typedType = type as ProviderType<RegistrateProvider>

            val prov = typedType.create(
                Context(
                    typedType, parent, event, known, event.generator.packOutput, event.existingFileHelper, lookup
                )
            )

            known.put(type, prov)
            if (sides.contains(prov.side())) {
                providers.put(type, prov)
            }
        }
    }

    override fun run(cache: CachedOutput): CompletableFuture<*> = registries.thenCompose { prov ->
        val futures = mutableListOf<CompletableFuture<*>>()

        for ((key, value) in providers.entries) {
            futures.add(value.run(cache))
        }

        return@thenCompose CompletableFuture.allOf(*futures.toTypedArray())
    }

    override fun getName(): String = "Registrate generator for ${parent.modId} (${
        providers.keys.fold("") { str, e ->
            return@fold str + Providers.getNameOf(e) + " "
        }
    })"
}