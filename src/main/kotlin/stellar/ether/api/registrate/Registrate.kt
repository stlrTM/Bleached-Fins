package stellar.ether.api.registrate

import com.google.common.base.Preconditions
import net.minecraft.core.Registry
import net.minecraft.resources.*
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.*
import net.neoforged.fml.loading.FMLEnvironment
import net.neoforged.neoforge.data.event.GatherDataEvent
import net.neoforged.neoforge.data.loading.DatagenModLoader
import net.neoforged.neoforge.registries.*
import stellar.ether.api.registrate.datagen.DataContainer
import stellar.ether.api.registrate.datagen.DataCtx
import stellar.ether.api.registrate.datagen.ProviderType
import stellar.ether.api.registrate.datagen.RegistrateProvider

fun whenDist(dist: Dist,act: () -> Unit) {
    if(FMLEnvironment.dist == dist) act()
}

/**
 * Key for registered entry
 *
 * @param R the registry type
 * @constructor Creates a new Key
 * @property type the resource key for the registry,has a type of [R]
 * @property key the location of registered entry
 */
data class Key<R>(
    val type: ResourceKey<out Registry<R>>,
    val key: ResourceLocation,
)

/**
 * Registered entry,holds the lazy entry and registers on event
 *
 * @param R the registry type
 * @param T the actual type of object registered,a subclass of [R]
 * @constructor Creates a new Entry
 * @property key the key for this entry
 * @property creator creates the object for this entry
 * @property entry the lazy entry for this registration,fills out when this entry gets registered
 * @property onRegister the list of listeners that get invoked once the event has invoked registration and entry is available
 */
data class Entry<R, T : R>(
    val key: Key<R>,
    val creator: () -> T,
    val entry: LazyEntry<R, T>,
    val builder: Builder<R, T, *, *>
) {
    private val onRegister: MutableList<(T) -> Unit> = mutableListOf()

    constructor(
        location: ResourceLocation,
        type: ResourceKey<out Registry<R>>,
        creator: () -> T,
        factory: (DeferredHolder<R, T>) -> LazyEntry<R, T>,
        builder: Builder<R, T, *, *>
    ) : this(
        Key(type, location),
        creator,
        factory(DeferredHolder.create(type, location)),
        builder
    )

    /**
     * Adds the callback that listens for registration
     *
     * @param callback
     */
    fun addCallback(callback: (T) -> Unit) {
        Preconditions.checkNotNull(callback, "Callback should not be null")
        onRegister.add(callback)
    }

    /**
     * Registers this entry
     *
     * @param event
     */
    fun register(event: RegisterEvent.RegisterHelper<T>) {
        val entry = creator()
        checkNotNull(entry)
        event.register(key.key, entry)
        onRegister.forEach { it.invoke(entry) }
    }
}

/**
 * Abstract registrate,the base for using registrate
 *
 * @param S self type
 * @constructor Creates a new AbstractRegistrate
 * @property modId the mod id
 */
abstract class AbstractRegistrate<S : AbstractRegistrate<S>>(
    val modId: String
) {
    val awaitingListeners: MutableMap<Class<out Event>, MutableList<(Event) -> Unit>> = mutableMapOf()
    var modBus: IEventBus? = null
        private set
    val registrations: MutableList<Entry<*, *>> = mutableListOf()
    val datagen by lazy { DataContainer(this) }


    fun <E : Event> registerEventListener(type: Class<E>, listener: (E) -> Unit) {
        val bus = modBus
        if (bus != null) {
            bus.addListener(type, listener)
        } else {
            @Suppress("UNCHECKED_CAST")
            awaitingListeners.getOrPut(type) { mutableListOf() }.add(listener as (Event) -> Unit)
        }
    }

    /**
     * Returns the entry for key
     *
     * @param R the registry type
     * @param T the actual type,a subclass of [R]
     * @param key the key of the registration
     * @param location the actual location of that entry
     * @return the lazy entry
     */
    fun <R, T : R> getBy(
        key: ResourceKey<out Registry<R>>,
        location: ResourceLocation,
    ): LazyEntry<R, T>? {
        val searchKey = Key(key, location)
        @Suppress("UNCHECKED_CAST") return registrations.firstOrNull { it.key == searchKey }?.entry as? LazyEntry<R, T>
    }

    fun <R> getAll(
        type: ResourceKey<out Registry<R>>
    ): List<LazyEntry<R,out R>> {
        @Suppress("UNCHECKED_CAST")
        return registrations.filter {
            it.key.type == type
        }.map { it.entry } as List<LazyEntry<R,out R>>
    }

    /**
     * Registers all the builders and datagen to the specified modbus
     *
     * @param bus the bus to register to
     */
    fun register(bus: IEventBus) {
        modBus = bus
        bus.addListener(::onRegisterEvent)

        awaitingListeners.forEach { (type, listeners) ->
            listeners.forEach { listener ->
                @Suppress("UNCHECKED_CAST")
                bus.addListener(type as Class<Event>, listener)
            }
        }
        awaitingListeners.clear()

        if (DatagenModLoader.isRunningDataGen()) {
            bus.addListener(::onGatherDataEvent)
        }
    }

    private fun onRegisterEvent(event: RegisterEvent) {
        registrations.filter { it.key.type == event.registryKey }.forEach { entry ->
            @Suppress("UNCHECKED_CAST") event.register(entry.key.type as ResourceKey<Registry<Any>>) { helper ->
                (entry as Entry<Any, Any>).register(helper)
            }
        }
    }

    private fun onGatherDataEvent(event: GatherDataEvent) {
        datagen.onData(event)
    }

    /**
     * Adds a datagen consumer for a provided type,creating an entry if it didn't exist
     *
     * @param T the type of provider
     * @param F the registry type
     * @param G the actual type,a subclass of [F]
     * @param builder builder which the consumer comes from
     * @param type the provider type
     * @param consumer the consumer of
     * @receiver the provider
     */
    fun <T : RegistrateProvider, F, G : F> addDataGenerator(
        builder: Builder<F, G, *, *>, type: ProviderType<T>, consumer: T.(DataCtx<F, G>) -> Unit
    ) {
        datagen.addProvider(builder, type, consumer)
    }

    /**
     * A function that is used internally by builders to submit an entry
     */
    fun <R, T : R> accept(
        builder: Builder<R, T, *, *>,
        name: String,
        type: ResourceKey<out Registry<R>>,
        factory: () -> T,
        entryFactory: (DeferredHolder<R, T>) -> LazyEntry<R, T> = { LazyEntry(this, it.key !!) }
    ): LazyEntry<R, T> {
        val location = ResourceLocation.fromNamespaceAndPath(modId, name)
        val entry = Entry(location, type, factory, entryFactory, builder)
        registrations.add(entry)
        return entry.entry
    }

    /**
     * A helper function for making DSL builders
     *
     * @param R the registry type
     * @param T the actual type,a subclass of [R]
     * @param P the parent type,usually the [stellar.ether.api.registrate.AbstractRegistrate]
     * @param B the builder type
     * @param builder the builder that is being used
     * @param action the action with a receiver for DSL
     * @receiver builder
     * @return the lazy entry
     */
    fun <R, T : R, P, B : Builder<R, T, P, *>> action(
        builder: B,
        action: B.() -> Unit
    ): LazyEntry<R, T> {
        builder.action()
        return builder.register()
    }
}