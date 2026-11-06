package stellar.ether.api.registrate.datagen

import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.neoforged.neoforge.common.data.ExistingFileHelper
import net.neoforged.neoforge.data.event.GatherDataEvent
import stellar.ether.api.registrate.AbstractRegistrate
import java.util.concurrent.CompletableFuture

data class Context<T : RegistrateProvider>(
    val type: ProviderType<T>,
    val parent: AbstractRegistrate<*>,
    val event: GatherDataEvent,
    val existing: Map<ProviderType<*>, RegistrateProvider>,
    val output: PackOutput,
    val fileHelper: ExistingFileHelper,
    val provider: CompletableFuture<HolderLookup.Provider>
) {
    @Suppress("UNCHECKED_CAST")
    fun <R : RegistrateProvider> get(type: ProviderType<R>): R = existing[type] as R

    fun genData(self: T) {
        parent.datagen.genData(type, self)
    }
}