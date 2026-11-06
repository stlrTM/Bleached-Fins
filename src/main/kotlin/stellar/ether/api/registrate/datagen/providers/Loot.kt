package stellar.ether.api.registrate.datagen.providers

import com.google.common.collect.BiMap
import net.minecraft.core.*
import net.minecraft.core.registries.Registries
import net.minecraft.data.loot.*
import net.minecraft.data.loot.packs.*
import net.minecraft.resources.*
import net.minecraft.util.ProblemReporter
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.animal.FrogVariant
import net.minecraft.world.item.Item
import net.minecraft.world.level.ItemLike
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.storage.loot.*
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer
import net.minecraft.world.level.storage.loot.functions.FunctionUserBuilder
import net.minecraft.world.level.storage.loot.parameters.*
import net.minecraft.world.level.storage.loot.predicates.*
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider
import net.neoforged.fml.LogicalSide
import net.neoforged.fml.util.ObfuscationReflectionHelper
import stellar.ether.api.registrate.AbstractRegistrate
import stellar.ether.api.registrate.datagen.*
import java.util.function.*
import java.util.stream.Stream

interface RegistrateLootTables : LootTableSubProvider {
    fun validate(writableRegistry: WritableRegistry<LootTable>, validationContext: ValidationContext) {}
}

class RegistrateBlockLootTables(
    provider: HolderLookup.Provider,
    private val parent: AbstractRegistrate<*>,
    private val callback: Consumer<RegistrateBlockLootTables>
) : VanillaBlockLoot(provider), RegistrateLootTables {

    val registries: HolderLookup.Provider = provider
    val itemLookup: HolderLookup<Item> = registries.lookupOrThrow(Registries.ITEM)
    val blockLookup: HolderLookup<Block> = registries.lookupOrThrow(Registries.BLOCK)
    val entityLookup: HolderLookup<EntityType<*>> = registries.lookupOrThrow(Registries.ENTITY_TYPE)

    override fun generate() {
        callback.accept(this)
    }

    override fun getKnownBlocks(): Iterable<Block> {
        return emptyList()
    }

    public override fun <T : FunctionUserBuilder<T>> applyExplosionDecay(item: ItemLike, functionBuilder: FunctionUserBuilder<T>): T = super.applyExplosionDecay(item, functionBuilder)
    public override fun <T : ConditionUserBuilder<T>> applyExplosionCondition(item: ItemLike, conditionBuilder: ConditionUserBuilder<T>): T = super.applyExplosionCondition(item, conditionBuilder)

    public override fun createSilkTouchDispatchTable(block: Block, builder: LootPoolEntryContainer.Builder<*>): LootTable.Builder = super.createSilkTouchDispatchTable(block, builder)
    public override fun createShearsDispatchTable(block: Block, builder: LootPoolEntryContainer.Builder<*>): LootTable.Builder = super.createShearsDispatchTable(block, builder)
    public override fun createSilkTouchOrShearsDispatchTable(block: Block, builder: LootPoolEntryContainer.Builder<*>): LootTable.Builder = super.createSilkTouchOrShearsDispatchTable(block, builder)

    public override fun createSingleItemTableWithSilkTouch(block: Block, item: ItemLike): LootTable.Builder = super.createSingleItemTableWithSilkTouch(block, item)
    public override fun createSingleItemTable(item: ItemLike, count: NumberProvider): LootTable.Builder = super.createSingleItemTable(item, count)
    public override fun createSingleItemTableWithSilkTouch(block: Block, item: ItemLike, count: NumberProvider): LootTable.Builder = super.createSingleItemTableWithSilkTouch(block, item, count)

    public override fun createSilkTouchOnlyTable(item: ItemLike): LootTable.Builder = super.createSilkTouchOnlyTable(item)
    public override fun createPotFlowerItemTable(item: ItemLike): LootTable.Builder = super.createPotFlowerItemTable(item)
    public override fun createSlabItemTable(block: Block): LootTable.Builder = super.createSlabItemTable(block)
    public override fun createNameableBlockEntityTable(block: Block): LootTable.Builder = super.createNameableBlockEntityTable(block)
    public override fun createShulkerBoxDrop(block: Block): LootTable.Builder = super.createShulkerBoxDrop(block)

    public override fun createCopperOreDrops(block: Block): LootTable.Builder = super.createCopperOreDrops(block)
    public override fun createLapisOreDrops(block: Block): LootTable.Builder = super.createLapisOreDrops(block)
    public override fun createRedstoneOreDrops(block: Block): LootTable.Builder = super.createRedstoneOreDrops(block)
    public override fun createBannerDrop(block: Block): LootTable.Builder = super.createBannerDrop(block)
    public override fun createBeeNestDrop(block: Block): LootTable.Builder = super.createBeeNestDrop(block)
    public override fun createBeeHiveDrop(block: Block): LootTable.Builder = super.createBeeHiveDrop(block)
    public override fun createCaveVinesDrop(block: Block): LootTable.Builder = super.createCaveVinesDrop(block)

    public override fun createOreDrop(block: Block, item: Item): LootTable.Builder = super.createOreDrop(block, item)
    public override fun createMushroomBlockDrop(block: Block, item: ItemLike): LootTable.Builder = super.createMushroomBlockDrop(block, item)
    public override fun createGrassDrops(block: Block): LootTable.Builder = super.createGrassDrops(block)

    public override fun createMultifaceBlockDrops(block: Block, builder: LootItemCondition.Builder): LootTable.Builder = super.createMultifaceBlockDrops(block, builder)
    public override fun createLeavesDrops(leavesBlock: Block, saplingBlock: Block, vararg chances: Float): LootTable.Builder = super.createLeavesDrops(leavesBlock, saplingBlock, *chances)
    public override fun createOakLeavesDrops(oakLeavesBlock: Block, saplingBlock: Block, vararg chances: Float): LootTable.Builder = super.createOakLeavesDrops(oakLeavesBlock, saplingBlock, *chances)
    public override fun createMangroveLeavesDrops(block: Block): LootTable.Builder = super.createMangroveLeavesDrops(block)
    public override fun createCropDrops(cropBlock: Block, grownCropItem: Item, seedsItem: Item, dropGrownCropCondition: LootItemCondition.Builder): LootTable.Builder = super.createCropDrops(cropBlock, grownCropItem, seedsItem, dropGrownCropCondition)
    public override fun createDoublePlantShearsDrop(sheared: Block): LootTable.Builder = super.createDoublePlantShearsDrop(sheared)
    public override fun createDoublePlantWithSeedDrops(block: Block, sheared: Block): LootTable.Builder = super.createDoublePlantWithSeedDrops(block, sheared)
    public override fun createCandleDrops(candleBlock: Block): LootTable.Builder = super.createCandleDrops(candleBlock)
    public override fun createPetalsDrops(petalBlock: Block): LootTable.Builder = super.createPetalsDrops(petalBlock)

    public override fun addNetherVinesDropTable(vines: Block, plant: Block) = super.addNetherVinesDropTable(vines, plant)
    public override fun createDoorTable(doorBlock: Block): LootTable.Builder = super.createDoorTable(doorBlock)
    public override fun dropPottedContents(flowerPot: Block) = super.dropPottedContents(flowerPot)
    public override fun otherWhenSilkTouch(block: Block, other: Block) = super.otherWhenSilkTouch(block, other)
    public override fun dropOther(block: Block, item: ItemLike) = super.dropOther(block, item)
    public override fun dropWhenSilkTouch(block: Block) = super.dropWhenSilkTouch(block)
    public override fun dropSelf(block: Block) = super.dropSelf(block)

    public override fun add(block: Block, builder: LootTable.Builder) = super.add(block, builder)

    companion object {
        fun createShearsOnlyDrop(item: ItemLike): LootTable.Builder = BlockLootSubProvider.createShearsOnlyDrop(item)
        fun createCandleCakeDrops(candleCakeBlock: Block): LootTable.Builder = BlockLootSubProvider.createCandleCakeDrops(candleCakeBlock)
    }
}

class RegistrateEntityLootTables(
    provider: HolderLookup.Provider,
    private val parent: AbstractRegistrate<*>,
    private val callback: Consumer<RegistrateEntityLootTables>
) : VanillaEntityLoot(provider), RegistrateLootTables {

    val registries: HolderLookup.Provider = provider
    val itemLookup: HolderLookup<Item> = registries.lookupOrThrow(Registries.ITEM)
    val blockLookup: HolderLookup<Block> = registries.lookupOrThrow(Registries.BLOCK)
    val entityLookup: HolderLookup<EntityType<*>> = registries.lookupOrThrow(Registries.ENTITY_TYPE)

    override fun generate() {
        callback.accept(this)
    }

    override fun getKnownEntityTypes(): java.util.stream.Stream<EntityType<*>> {
        return Stream.of(*parent.getAll(Registries.ENTITY_TYPE).map { it.get() }.toTypedArray())
    }

    override fun canHaveLootTable(entityType: EntityType<*>): Boolean = super.canHaveLootTable(entityType)
    override fun killedByFrogVariant(frogVariant: ResourceKey<FrogVariant>): LootItemCondition.Builder = super.killedByFrogVariant(frogVariant)

    public override fun add(entityType: EntityType<*>, builder: LootTable.Builder) = super.add(entityType, builder)
    public override fun add(entityType: EntityType<*>, defaultLootTable: ResourceKey<LootTable>, builder: LootTable.Builder) = super.add(entityType, defaultLootTable, builder)

    companion object {
        fun createSheepTable(woolItem: ItemLike): LootTable.Builder = EntityLootSubProvider.createSheepTable(woolItem)
    }
}

class Loot(
    override val ctx: Context<Loot>
) : LootTableProvider(
    ctx.output,
    emptySet(),
    VanillaLootTableProvider.create(ctx.output, ctx.provider).tables,
    ctx.provider,
), RegistrateProvider {

    interface LootType<T : RegistrateLootTables> {
        val lootSet: LootContextParamSet
        fun getLootCreator(provider: HolderLookup.Provider, parent: AbstractRegistrate<*>, callback: Consumer<T>): T

        companion object {
            val LOOT_TYPES = mutableMapOf<String, LootType<*>>()

            val BLOCK = register("block", LootContextParamSets.BLOCK, ::RegistrateBlockLootTables)
            val ENTITY = register("entity", LootContextParamSets.ENTITY, ::RegistrateEntityLootTables)

            fun <T : RegistrateLootTables> register(
                name: String,
                set: LootContextParamSet,
                factory: (HolderLookup.Provider, AbstractRegistrate<*>, Consumer<T>) -> T
            ): LootType<T> {
                val type = object : LootType<T> {
                    override val lootSet = set
                    override fun getLootCreator(provider: HolderLookup.Provider, parent: AbstractRegistrate<*>, callback: Consumer<T>): T =
                        factory(provider, parent, callback)
                }
                LOOT_TYPES[name] = type
                return type
            }
        }
    }

    private val specialLootActions = mutableMapOf<LootType<*>, MutableList<Consumer<in RegistrateLootTables>>>()
    private val lootActions = mutableMapOf<LootContextParamSet, MutableList<Consumer<BiConsumer<ResourceKey<LootTable>, LootTable.Builder>>>>()
    private val currentLootCreators = mutableSetOf<RegistrateLootTables>()

    val provider: HolderLookup.Provider
        get() = ctx.provider.getNow(null) ?: error("Lookup provider is not yet resolved")

    fun <T> resolve(key: ResourceKey<T>): Holder<T> =
        provider.lookupOrThrow(key.registryKey()).getOrThrow(key)

    override fun side(): LogicalSide = LogicalSide.SERVER

    override fun validate(
        writableRegistry: WritableRegistry<LootTable>,
        validationContext: ValidationContext,
        problemReporter: ProblemReporter.Collector
    ) {
        currentLootCreators.forEach { it.validate(writableRegistry, validationContext) }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : RegistrateLootTables> addLootAction(type: LootType<T>, action: T.() -> Unit) {
        specialLootActions.getOrPut(type) { mutableListOf() }.add {
            itt -> action(itt as T)
        }
    }

    fun addLootAction(set: LootContextParamSet, action: Consumer<BiConsumer<ResourceKey<LootTable>, LootTable.Builder>>) {
        lootActions.getOrPut(set) { mutableListOf() }.add(action)
    }

    private fun getLootCreator(provider: HolderLookup.Provider, parent: AbstractRegistrate<*>, type: LootType<*>): LootTableSubProvider {
        val creator = type.getLootCreator(provider, parent) { cons ->
            specialLootActions[type]?.forEach { it.accept(cons) }
        }
        currentLootCreators.add(creator)
        return creator
    }

    override fun getTables(): List<SubProviderEntry> {
        ctx.genData(this)
        currentLootCreators.clear()

        return buildList {
            LootType.LOOT_TYPES.values.forEach { type ->
                add(SubProviderEntry({ provider -> getLootCreator(provider, ctx.parent, type) }, type.lootSet))
            }
            SET_REGISTRY.values.forEach { set ->
                add(SubProviderEntry({ _ ->
                    LootTableSubProvider { callback ->
                        lootActions[set]?.forEach { it.accept(callback) }
                    }
                }, set))
            }
        }
    }

    companion object {
        private val SET_REGISTRY: BiMap<ResourceLocation, LootContextParamSet> by lazy {
            ObfuscationReflectionHelper.getPrivateValue(LootContextParamSets::class.java, null, "REGISTRY")
                ?: throw IllegalStateException("Failed to access LootContextParamSets.REGISTRY via reflection")
        }
    }
}