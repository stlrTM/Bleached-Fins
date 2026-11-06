package stellar.ether.api.registrate.datagen.providers

import net.minecraft.core.*
import net.minecraft.data.CachedOutput
import net.minecraft.data.registries.RegistryPatchGenerator
import net.minecraft.data.worldgen.BootstrapContext
import net.minecraft.resources.ResourceKey
import net.neoforged.fml.LogicalSide
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider
import stellar.ether.api.registrate.datagen.*
import java.util.concurrent.CompletableFuture
import java.util.function.Function

class Datapack private constructor(
    override val ctx: Context<Datapack>,
    private val builder: RegistrySetBuilder
) : RegistrateProvider, DatapackBuiltinEntriesProvider(
    ctx.output,
    ctx.provider,
    builder,
    setOf(ctx.parent.modId)
) {
    constructor(ctx: Context<Datapack>) : this(ctx, RegistrySetBuilder())

    private val available: MutableMap<ResourceKey<Registry<*>>, MutableList<RegistrySetBuilder.RegistryBootstrap<*>>> = mutableMapOf()

    override fun side(): LogicalSide = LogicalSide.SERVER

    override fun run(p0: CachedOutput): CompletableFuture<*> {
        ctx.genData(this)
        this.registries = RegistryPatchGenerator.createLookup(registries, builder)
            .thenApply(RegistrySetBuilder.PatchedRegistries::patches)
        return super.run(p0)
    }

    // говно
    fun <T> add(key: ResourceKey<Registry<T>>, boot: RegistrySetBuilder.RegistryBootstrap<T>) {
        available.getOrPut(key as ResourceKey<Registry<*>>) {
            builder.add(key) { ctx ->
                for(boot in available[key]!!) {
                    (boot as RegistrySetBuilder.RegistryBootstrap<T>).run(ctx)
                }
            }
            return@getOrPut mutableListOf()
        }.add(boot)
    }
}