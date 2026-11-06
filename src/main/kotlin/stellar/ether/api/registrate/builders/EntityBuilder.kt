package stellar.ether.api.registrate.builders

import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.renderer.entity.*
import net.minecraft.core.registries.Registries
import net.minecraft.resources.*
import net.minecraft.world.entity.*
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.item.*
import net.minecraft.world.level.Level
import net.minecraft.world.level.levelgen.Heightmap
import net.neoforged.api.distmarker.Dist
import net.neoforged.fml.loading.FMLEnvironment
import net.neoforged.neoforge.client.event.EntityRenderersEvent
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers
import net.neoforged.neoforge.event.entity.*
import net.neoforged.neoforge.registries.DeferredHolder
import stellar.ether.api.registrate.AbstractRegistrate
import stellar.ether.api.registrate.LazyEntry
import stellar.ether.api.registrate.builders.ItemBuilders.item
import stellar.ether.api.registrate.datagen.Providers
import stellar.ether.api.registrate.whenDist

class EntityBuilder<T : Entity,P>(
    owner: AbstractRegistrate<*>,
    parent: P,
    name: String,
    factory: EntityType.EntityFactory<T>,
    category: MobCategory,
) : BuilderWithSettings<EntityType.Builder<T>, EntityType<*>, EntityType<T>, P, EntityBuilder<T, P>>(
    owner,
    parent,
    name,
    Registries.ENTITY_TYPE,
    { EntityType.Builder.of(factory,category) },
    { a -> a.build(name) }
) {
    private var renderer: (() -> (EntityRendererProvider.Context) -> EntityRenderer<in T>)? = null
    private var spawnConfigured = false
    private var attributes = false
    private var layer = false

    fun spawnEgg(
        bg: Int,
        hl: Int,
        additionalConfig: ItemBuilder<SpawnEggItem, out AbstractRegistrate<*>>.() -> Unit = {}
    ): ItemEntry<SpawnEggItem> = owner.item(
        name+"_spawn_egg",
        {
            SpawnEggItem(
                this.entry!!.get() as EntityType<out Mob>,
                bg,
                hl,
                it
            )
        },
        {
            val thiz = this
            tab(CreativeModeTabs.SPAWN_EGGS)
            addData(Providers.ITEM_MODELS) { ctx ->
                withExistingParent(
                    ctx.name,
                    ResourceLocation.withDefaultNamespace(
                        "item/template_spawn_egg"
                    )
                )
            }
            additionalConfig.invoke(thiz)
        }
    )

    fun layer(layerSupplier: () -> ModelLayerLocation,definitionSupplier: () -> LayerDefinition) {
        check(!this.layer) { "Layer is registered already" }
        addEventListener(EntityRenderersEvent.RegisterLayerDefinitions::class.java) {
            registerLayerDefinition(
                layerSupplier(),
                definitionSupplier
            )
        }
    }

    fun renderer(renderer: () -> (EntityRendererProvider.Context) -> EntityRenderer<in T>) {
        if (this.renderer == null && FMLEnvironment.dist.isClient) {
            whenDist(Dist.CLIENT) { this.registerRenderer() }
        }
        this.renderer = renderer
    }

    private fun registerRenderer() {
        addEventListener(RegisterRenderers::class.java) {
            val renderer = this@EntityBuilder.renderer
            if (renderer != null) {
                try {
                    val provider = renderer()
                    registerEntityRenderer(entry!!.get()) {
                        provider.invoke(it)
                    }
                } catch (e: Exception) {
                    throw IllegalStateException("Failed to register renderer for Entity $name", e)
                }
            }
        }
    }

    fun attributes(act: () -> AttributeSupplier.Builder) {
        check(!attributes) { "Attributes have already been registered" }
        addEventListener(EntityAttributeCreationEvent::class.java) {
            put(entry!!.get() as EntityType<out LivingEntity>, act().build())
        }
    }

    fun spawnPlacement(
        type: SpawnPlacementType,
        heightmap: Heightmap.Types,
        predicate: SpawnPlacements.SpawnPredicate<T>,
        operation: RegisterSpawnPlacementsEvent.Operation
    ) {
        check(!spawnConfigured) { "Cannot configure spawn placement more than once" }
        spawnConfigured = true
        addEventListener(RegisterSpawnPlacementsEvent::class.java) {
            register(
                entry!!.get(),
                type,
                heightmap, predicate, operation
            )
        }
    }

    override fun createEntryWrapper(holder: DeferredHolder<EntityType<*>, EntityType<T>>): EntityEntry<T> {
        return EntityEntry(owner,holder.key!!)
    }
}

object EntityBuilders {
    fun <T : Entity,S : AbstractRegistrate<S>> AbstractRegistrate<S>.entity(
        name: String,
        factory: EntityType.EntityFactory<T>,
        category: MobCategory,
        action: EntityBuilder<T, AbstractRegistrate<S>>.() -> Unit
    ): EntityEntry<T> = entity(
        name,
        factory,
        category,
        this,
        action
    )

    fun <T : Entity,P> AbstractRegistrate<*>.entity(
        name: String,
        factory: EntityType.EntityFactory<T>,
        category: MobCategory,
        parent: P,
        action: EntityBuilder<T, P>.() -> Unit
    ): EntityEntry<T> = action(EntityBuilder(this,parent,name,factory,category),action) as EntityEntry<T>
}

class EntityEntry<T : Entity>(
    owner: AbstractRegistrate<*>,
    key: ResourceKey<EntityType<*>>,
) : LazyEntry<EntityType<*>, EntityType<T>>(
    owner,
    key
) {
    fun create(level: Level): T = get().create(level)!!
}