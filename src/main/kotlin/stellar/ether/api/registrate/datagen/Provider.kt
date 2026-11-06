package stellar.ether.api.registrate.datagen

import net.minecraft.core.HolderLookup
import net.minecraft.data.DataProvider
import net.neoforged.fml.LogicalSide
import java.util.concurrent.CompletableFuture

interface RegistrateProvider : DataProvider {
    val ctx: Context<out RegistrateProvider>
    fun side(): LogicalSide
}

interface RegistrateFilledLookupProvider : RegistrateProvider {
    fun lookup(): CompletableFuture<HolderLookup.Provider>
}

interface ProviderType<T : RegistrateProvider> {
    fun create(ctx: Context<T>): T

    fun dependencies(): List<ProviderType<*>>
}

