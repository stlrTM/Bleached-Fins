package stellar.ether.api.registrate.datagen.providers

import net.minecraft.advancements.Advancement
import net.minecraft.advancements.AdvancementHolder
import net.minecraft.core.Holder
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.data.CachedOutput
import net.minecraft.data.recipes.*
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.Item
import net.minecraft.world.item.crafting.*
import net.minecraft.world.level.ItemLike
import net.minecraft.world.level.block.Block
import net.neoforged.fml.LogicalSide
import net.neoforged.neoforge.common.Tags
import net.neoforged.neoforge.common.conditions.ICondition
import stellar.ether.api.registrate.datagen.Context
import stellar.ether.api.registrate.datagen.RegistrateProvider
import java.util.concurrent.CompletableFuture

class Recipes(
    override val ctx: Context<Recipes>
) : RecipeProvider(ctx.output, ctx.provider), RegistrateProvider, RecipeOutput {

    private var lookupProvider: HolderLookup.Provider? = null
    private var callback: RecipeOutput? = null

    override fun side(): LogicalSide = LogicalSide.SERVER

    override fun run(output: CachedOutput, provider: HolderLookup.Provider): CompletableFuture<*> {
        this.lookupProvider = provider
        return super.run(output, provider)
    }

    fun getRegistries(): HolderLookup.Provider {
        return lookupProvider ?: error("Attempting to resolve registries before provider is being run")
    }

    fun itemLookup(): HolderLookup<Item> = getRegistries().lookupOrThrow(Registries.ITEM)
    fun blockLookup(): HolderLookup<Block> = getRegistries().lookupOrThrow(Registries.BLOCK)
    fun entityLookup(): HolderLookup<EntityType<*>> = getRegistries().lookupOrThrow(Registries.ENTITY_TYPE)

    fun <T> resolve(key: ResourceKey<T>): Holder<T> {
        return getRegistries().lookupOrThrow(key.registryKey()).getOrThrow(key)
    }

    override fun accept(id: ResourceLocation, recipe: Recipe<*>, advancement: AdvancementHolder?, vararg conditions: ICondition) {
        val cb = callback ?: error("Cannot accept recipes outside of a call to buildRecipes")
        cb.accept(id, recipe, advancement, *conditions)
    }

    override fun advancement(): Advancement.Builder {
        val cb = callback ?: error("Cannot get advancement outside of a call to buildRecipes")
        return cb.advancement()
    }

    override fun buildRecipes(recipeOutput: RecipeOutput) {
        this.callback = recipeOutput
        // Trigger Registrate builders for recipes
        ctx.genData(this)
        this.callback = null
    }

    fun safeId(id: ResourceLocation): ResourceLocation = ResourceLocation.fromNamespaceAndPath(ctx.parent.modId, safeName(id))
    fun safeId(source: DataIngredient): ResourceLocation = safeId(source.id)
    fun safeId(registryEntry: ItemLike): ResourceLocation = safeId(BuiltInRegistries.ITEM.getKey(registryEntry.asItem()))

    fun safeName(id: ResourceLocation): String = id.path.replace('/', '_')
    fun safeName(source: DataIngredient): String = safeName(source.id)
    fun safeName(registryEntry: ItemLike): String = safeName(BuiltInRegistries.ITEM.getKey(registryEntry.asItem()))

    private fun ResourceLocation.withSuffix(suffix: String): ResourceLocation {
        return ResourceLocation.fromNamespaceAndPath(this.namespace, this.path + suffix)
    }

    companion object {
        const val DEFAULT_SMELT_TIME = 200
        const val DEFAULT_BLAST_TIME = DEFAULT_SMELT_TIME / 2
        const val DEFAULT_SMOKE_TIME = DEFAULT_BLAST_TIME
        const val DEFAULT_CAMPFIRE_TIME = DEFAULT_SMELT_TIME * 3

        private val COOKING_TYPE_NAMES = mapOf(
            RecipeSerializer.SMELTING_RECIPE to "smelting",
            RecipeSerializer.BLASTING_RECIPE to "blasting",
            RecipeSerializer.SMOKING_RECIPE to "smoking",
            RecipeSerializer.CAMPFIRE_COOKING_RECIPE to "campfire"
        )
    }

    fun <T : ItemLike, S : AbstractCookingRecipe> cooking(
        source: DataIngredient, category: RecipeCategory, result: () -> T,
        experience: Float, cookingTime: Int, serializer: RecipeSerializer<S>, factory: AbstractCookingRecipe.Factory<S>
    ) {
        val typeName = COOKING_TYPE_NAMES[serializer] ?: "cooking"
        cooking(source, category, result, experience, cookingTime, typeName, serializer, factory)
    }

    fun <T : ItemLike, S : AbstractCookingRecipe> cooking(
        source: DataIngredient, category: RecipeCategory, result: () -> T,
        experience: Float, cookingTime: Int, typeName: String,
        serializer: RecipeSerializer<S>, factory: AbstractCookingRecipe.Factory<S>
    ) {
        val resultItem = result().asItem()
        SimpleCookingRecipeBuilder.generic(source.toVanilla(), category, resultItem, experience, cookingTime, serializer, factory)
            .unlockedBy("has_${safeName(source)}", source.getCriterion(this))
            .save(this, safeId(resultItem).withSuffix("_from_${safeName(source)}_$typeName"))
    }

    fun <T : ItemLike> smelting(source: DataIngredient, category: RecipeCategory, result: () -> T, experience: Float, cookingTime: Int = DEFAULT_SMELT_TIME) {
        cooking(source, category, result, experience, cookingTime, RecipeSerializer.SMELTING_RECIPE, ::SmeltingRecipe)
    }

    fun <T : ItemLike> blasting(source: DataIngredient, category: RecipeCategory, result: () -> T, experience: Float, cookingTime: Int = DEFAULT_BLAST_TIME) {
        cooking(source, category, result, experience, cookingTime, RecipeSerializer.BLASTING_RECIPE, ::BlastingRecipe)
    }

    fun <T : ItemLike> smoking(source: DataIngredient, category: RecipeCategory, result: () -> T, experience: Float, cookingTime: Int = DEFAULT_SMOKE_TIME) {
        cooking(source, category, result, experience, cookingTime, RecipeSerializer.SMOKING_RECIPE, ::SmokingRecipe)
    }

    fun <T : ItemLike> campfire(source: DataIngredient, category: RecipeCategory, result: () -> T, experience: Float, cookingTime: Int = DEFAULT_CAMPFIRE_TIME) {
        cooking(source, category, result, experience, cookingTime, RecipeSerializer.CAMPFIRE_COOKING_RECIPE, ::CampfireCookingRecipe)
    }

    fun <T : ItemLike> smeltingAndBlasting(source: DataIngredient, category: RecipeCategory, result: () -> T, xp: Float) {
        smelting(source, category, result, xp)
        blasting(source, category, result, xp)
    }

    fun <T : ItemLike> food(source: DataIngredient, category: RecipeCategory, result: () -> T, xp: Float) {
        smelting(source, category, result, xp)
        smoking(source, category, result, xp)
        campfire(source, category, result, xp)
    }

    fun <T : ItemLike> stonecutting(source: DataIngredient, category: RecipeCategory, result: () -> T, resultAmount: Int = 1) {
        val resultItem = result().asItem()
        SingleItemRecipeBuilder.stonecutting(source.toVanilla(), category, resultItem, resultAmount)
            .unlockedBy("has_${safeName(source)}", source.getCriterion(this))
            .save(this, safeId(resultItem).withSuffix("_from_${safeName(source)}_stonecutting"))
    }

    fun <T : ItemLike> square(source: DataIngredient, category: RecipeCategory, output: () -> T, small: Boolean) {
        val builder = ShapedRecipeBuilder.shaped(category, output().asItem())
            .define('X', source.toVanilla())
        
        if (small) {
            builder.pattern("XX").pattern("XX")
        } else {
            builder.pattern("XXX").pattern("XXX").pattern("XXX")
        }
        
        builder.unlockedBy("has_${safeName(source)}", source.getCriterion(this))
            .save(this, safeId(output()))
    }

    fun <T : ItemLike> storage(source: () -> T, category: RecipeCategory, output: () -> T) {
        storage(DataIngredient.items(source()), category, source, DataIngredient.items(output()), output)
    }

    fun <T : ItemLike> storage(
        sourceIngredient: DataIngredient, category: RecipeCategory, source: () -> T,
        outputIngredient: DataIngredient, output: () -> T
    ) {
        square(sourceIngredient, category, output, false)
        singleItemUnfinished(outputIngredient, category, source, 1, 9)
            .save(this, safeId(sourceIngredient).withSuffix("_from_${safeName(output())}"))
    }

    fun <T : ItemLike> singleItemUnfinished(source: DataIngredient, category: RecipeCategory, result: () -> T, required: Int, amount: Int): ShapelessRecipeBuilder {
        return ShapelessRecipeBuilder.shapeless(category, result().asItem(), amount)
            .requires(source.toVanilla(), required)
            .unlockedBy("has_${safeName(source)}", source.getCriterion(this))
    }

    fun <T : ItemLike> singleItem(source: DataIngredient, category: RecipeCategory, result: () -> T, required: Int, amount: Int) {
        singleItemUnfinished(source, category, result, required, amount).save(this, safeId(result()))
    }

    fun <T : ItemLike> planks(source: DataIngredient, category: RecipeCategory, result: () -> T) {
        singleItemUnfinished(source, category, result, 1, 4)
            .group("planks")
            .save(this, safeId(result()))
    }

    fun <T : ItemLike> stairs(source: DataIngredient, category: RecipeCategory, result: () -> T, group: String?, stone: Boolean) {
        val builder = ShapedRecipeBuilder.shaped(category, result().asItem(), 4)
            .pattern("X  ").pattern("XX ").pattern("XXX")
            .define('X', source.toVanilla())
            .unlockedBy("has_${safeName(source)}", source.getCriterion(this))
        
        if (group != null) builder.group(group)
        builder.save(this, safeId(result()))
        
        if (stone) stonecutting(source, category, result)
    }

    fun <T : ItemLike> slab(source: DataIngredient, category: RecipeCategory, result: () -> T, group: String?, stone: Boolean) {
        val builder = ShapedRecipeBuilder.shaped(category, result().asItem(), 6)
            .pattern("XXX")
            .define('X', source.toVanilla())
            .unlockedBy("has_${safeName(source)}", source.getCriterion(this))
        
        if (group != null) builder.group(group)
        builder.save(this, safeId(result()))
        
        if (stone) stonecutting(source, category, result, 2)
    }

    fun <T : ItemLike> fence(source: DataIngredient, category: RecipeCategory, result: () -> T, group: String?) {
        val builder = ShapedRecipeBuilder.shaped(category, result().asItem(), 3)
            .pattern("W#W").pattern("W#W")
            .define('W', source.toVanilla())
            .define('#', Tags.Items.RODS_WOODEN)
            .unlockedBy("has_${safeName(source)}", source.getCriterion(this))
        
        if (group != null) builder.group(group)
        builder.save(this, safeId(result()))
    }

    fun <T : ItemLike> fenceGate(source: DataIngredient, category: RecipeCategory, result: () -> T, group: String?) {
        val builder = ShapedRecipeBuilder.shaped(category, result().asItem())
            .pattern("#W#").pattern("#W#")
            .define('W', source.toVanilla())
            .define('#', Tags.Items.RODS_WOODEN)
            .unlockedBy("has_${safeName(source)}", source.getCriterion(this))
        
        if (group != null) builder.group(group)
        builder.save(this, safeId(result()))
    }

    fun <T : ItemLike> wall(source: DataIngredient, category: RecipeCategory, result: () -> T) {
        ShapedRecipeBuilder.shaped(category, result().asItem(), 6)
            .pattern("XXX").pattern("XXX")
            .define('X', source.toVanilla())
            .unlockedBy("has_${safeName(source)}", source.getCriterion(this))
            .save(this, safeId(result()))
        stonecutting(source, category, result)
    }

    fun <T : ItemLike> door(source: DataIngredient, category: RecipeCategory, result: () -> T, group: String?) {
        val builder = ShapedRecipeBuilder.shaped(category, result().asItem(), 3)
            .pattern("XX").pattern("XX").pattern("XX")
            .define('X', source.toVanilla())
            .unlockedBy("has_${safeName(source)}", source.getCriterion(this))
        
        if (group != null) builder.group(group)
        builder.save(this, safeId(result()))
    }

    fun <T : ItemLike> trapDoor(source: DataIngredient, category: RecipeCategory, result: () -> T, group: String?) {
        val builder = ShapedRecipeBuilder.shaped(category, result().asItem(), 2)
            .pattern("XXX").pattern("XXX")
            .define('X', source.toVanilla())
            .unlockedBy("has_${safeName(source)}", source.getCriterion(this))
        
        if (group != null) builder.group(group)
        builder.save(this, safeId(result()))
    }
    
    // Note: The static mapped protected functions from Java RecipeProvider (e.g., netheriteSmithing, trapdoorBuilder)
    // are automatically accessible in Kotlin due to standard inheritance scoping rules. You can call them natively 
    // without explicitly exposing them here.
}