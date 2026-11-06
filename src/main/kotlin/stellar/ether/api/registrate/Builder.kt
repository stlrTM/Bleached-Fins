package stellar.ether.api.registrate

import net.minecraft.core.Registry
import net.minecraft.resources.*
import net.neoforged.bus.api.Event
import stellar.ether.api.registrate.datagen.DataCtx
import stellar.ether.api.registrate.datagen.ProviderType
import stellar.ether.api.registrate.datagen.RegistrateProvider

/**
 * Registrate DSL marker
 *
 * @constructor Creates a new RegistrateDSL
 */
@DslMarker
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
annotation class RDSL

/**
 * Base builder class for all builder, you will likely use [AbstractBuilder]
 *
 * @param R the registry type
 * @param T the current building type,always a subclass of [R]
 * @param P the parent type,usually [AbstractRegistrate]
 * @param S the self type,a recursive type for support of building nested patterns
 * @property owner the owner registrate
 * @property parent the parent of this builder
 * @property name the name of this builder
 * @property type the resource key for [R]
 * @property location the actual name this gets registered as
 */
@RDSL
interface Builder<R, T : R, P, S : Builder<R, T, P, S>> : () -> LazyEntry<R, T> {
    val owner: AbstractRegistrate<*>
    val parent: P
    val name: String
    val type: ResourceKey<out Registry<R>>

    val location: ResourceLocation
        get() = ResourceLocation.fromNamespaceAndPath(owner.modId, name)

    val entry: LazyEntry<R, T>?
        get() = owner.getBy(type, location)

    fun <E : Event> addEventListener(type: Class<E>, listener: E.() -> Unit) {
        owner.registerEventListener(type, listener)
    }

    /**
     * Adds a datagen provider for this entry,generating at datagen run
     *
     * @param D type of the provider
     * @param provider the provider type
     * @param consumer the action to perform with the provider
     * @receiver the provider
     */
    fun <D : RegistrateProvider> addData(provider: ProviderType<D>, consumer: @RDSL D.(DataCtx<R, T>) -> Unit) {
        owner.addDataGenerator(this, provider, consumer)
    }

    /**
     * Registers this builder
     *
     * @return the lazy entry
     */
    fun register(): LazyEntry<R, T>

    override fun invoke(): LazyEntry<R, T> = register()
}