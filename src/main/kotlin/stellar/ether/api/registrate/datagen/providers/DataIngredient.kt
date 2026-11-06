package stellar.ether.api.registrate.datagen.providers

import net.minecraft.advancements.CriteriaTriggers
import net.minecraft.advancements.Criterion
import net.minecraft.advancements.critereon.InventoryChangeTrigger
import net.minecraft.advancements.critereon.ItemPredicate
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.data.recipes.RecipeProvider
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.level.ItemLike
import java.util.Optional

class DataIngredient private constructor(
    private val idFactory: () -> ResourceLocation,
    private val ingredientFactory: () -> Ingredient,
    private val criterionFactory: (RecipeProvider) -> Criterion<InventoryChangeTrigger.TriggerInstance>
) {
    // Lazily evaluated to prevent premature registry resolution crashes during static initialization
    val id: ResourceLocation get() = idFactory()

    fun toVanilla(): Ingredient = ingredientFactory()

    fun getCriterion(provider: RecipeProvider): Criterion<InventoryChangeTrigger.TriggerInstance> {
        return criterionFactory(provider)
    }

    companion object {
        /**
         * Creates a DataIngredient from one or more directly specified ItemLike instances.
         */
        fun items(vararg items: ItemLike): DataIngredient {
            require(items.isNotEmpty()) { "DataIngredient requires at least one item" }
            return DataIngredient(
                idFactory = { BuiltInRegistries.ITEM.getKey(items[0].asItem()) },
                ingredientFactory = { Ingredient.of(*items) },
                criterionFactory = { InventoryChangeTrigger.TriggerInstance.hasItems(*items) }
            )
        }

        /**
         * Creates a DataIngredient from a lazily evaluated ItemLike supplier.
         */
        fun items(item: () -> ItemLike): DataIngredient {
            return DataIngredient(
                idFactory = { BuiltInRegistries.ITEM.getKey(item().asItem()) },
                ingredientFactory = { Ingredient.of(item()) },
                criterionFactory = { InventoryChangeTrigger.TriggerInstance.hasItems(item()) }
            )
        }

        /**
         * Creates a DataIngredient tracking an entire Item TagKey.
         */
        fun tag(tag: TagKey<Item>): DataIngredient {
            return DataIngredient(
                idFactory = { tag.location() },
                ingredientFactory = { Ingredient.of(tag) },
                criterionFactory = {
                    // Replicates the behavior of RecipeProvider.inventoryTrigger internally
                    // since we are bypassing the protected Java access scope.
                    val predicate = ItemPredicate.Builder.item().of(tag).build()
                    CriteriaTriggers.INVENTORY_CHANGED.createCriterion(
                        InventoryChangeTrigger.TriggerInstance(
                            Optional.empty(),
                            InventoryChangeTrigger.TriggerInstance.Slots.ANY,
                            listOf(predicate)
                        )
                    )
                }
            )
        }
    }
}