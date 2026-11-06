package stellar.aquatic.index

import com.google.gson.*
import net.minecraft.advancements.*
import net.minecraft.advancements.critereon.ImpossibleTrigger
import net.minecraft.core.component.DataComponents
import net.minecraft.core.dispenser.ProjectileDispenseBehavior
import net.minecraft.data.recipes.*
import net.minecraft.sounds.SoundEvents
import net.minecraft.tags.ItemTags
import net.minecraft.world.effect.*
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.item.*
import net.minecraft.world.item.component.CustomData
import net.minecraft.world.item.component.CustomModelData
import net.minecraft.world.item.crafting.*
import net.minecraft.world.level.block.DispenserBlock
import net.minecraft.world.level.material.Fluids
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder
import net.neoforged.neoforge.client.model.generators.ModelFile.UncheckedModelFile
import net.neoforged.neoforge.client.model.generators.loaders.SeparateTransformsModelBuilder
import stellar.aquatic.Aquatic.registrate
import stellar.aquatic.Aquatic.tab
import stellar.aquatic.content.item.SturgeonBucketItem
import stellar.aquatic.content.item.piranha.PiranhaArrowItem
import stellar.aquatic.loc
import stellar.ether.api.Loadable
import stellar.ether.api.registrate.builders.ItemBuilders.item
import stellar.ether.api.registrate.datagen.Providers
import stellar.ether.api.registrate.datagen.providers.DataIngredient

object AquaticItems : Loadable {
//    val angrybird = registrate.item(
//        "angry_bird",
//        ::ZloyPtichkaItem,
//    ) {
//        properties {
//            stacksTo(1)
//        }
//
//        addData(Providers.LANG) { ctx ->
//            add(ctx.get(), "красны злой птица")
//        }
//        addData(Providers.ITEM_MODELS) { ctx ->
//            generated { ctx.get() }
//        }
//    }
//
//    val mistertaksa = registrate.item(
//        "mistertaksa",
//        ::TaksaItem
//    ) {
//
//        addData(Providers.LANG) { ctx ->
//            add(ctx.get(), "мистертакса")
//        }
//        addData(Providers.ITEM_MODELS) { ctx ->
//            generated { ctx.get() }
//        }
//
//    }
//
//    val burger = registrate.item(
//        "tainstvenniy_chelovek_burger",
//        ::Item
//    ) {
//        properties {
//            stacksTo(1)
//            food(
//                FoodProperties.Builder()
//                    .nutrition(2100 * 2)
//                    .saturationModifier(.5f)
//                    .alwaysEdible()
//                    .fast()
//                    .build()
//            )
//        }
//
//        addData(Providers.LANG) { ctx ->
//            add(ctx.get(), "добавь Таинственный человек с бурегром айтем")
//        }
//        addData(Providers.ITEM_MODELS) { ctx ->
//            generated(ctx)
//        }
//
//    }

    val sturgeonBucket = registrate.item("sturgeon_bucket", { SturgeonBucketItem(
        AquaticEntities.sturgeon.get(),
        Fluids.WATER,
        SoundEvents.BUCKET_EMPTY_FISH,
        it
    )}) {
        properties {
            component(DataComponents.BUCKET_ENTITY_DATA, CustomData.EMPTY)
        }

        addData(Providers.LANG) { ctx ->
            add(ctx(),"Sturgeon in a Bucket")
            add(ctx().descriptionId+"_baby","Baby Sturgeon in a Bucket")
        }
        addData(Providers.ITEM_MODELS) { ctx ->
            val base = generated(ctx)

            val baby = getBuilder(name(ctx)+"_baby").parent(UncheckedModelFile("item/generated"))
            baby.texture("layer0",modLoc("item/"+name(ctx)+"_baby"))

            withExistingParent("item/sturgeon_bucket","item/generated")
                .override()
                    .model(base)
                    .predicate(mcLoc("custom_model_data"),0f)
                .end()
                .override()
                    .model(baby)
                    .predicate(mcLoc("custom_model_data"),1f)
                .end()
        }

        tab(tab)
    }

    val sturgeonItem = registrate.item("sturgeon_item",::Item) {
        properties {
            food(
                FoodProperties.Builder()
                    .fast()
                    .nutrition(2)
                    .saturationModifier(2f)
                    .build()
            )
        }

        addData(Providers.LANG) { ctx ->
            add(ctx(),"Sturgeon")
        }
        addData(Providers.ITEM_MODELS) { ctx ->
            generated(ctx)
        }

        tab(tab)
    
        addData(Providers.ITEM_TAGS) { ctx -> add(ItemTags.FISHES).add(ctx()) }
    }

    val sturgeonCaviarItem = registrate.item("sturgeon_caviar", { BlockItem(AquaticBlocks.sturgeonCaviar.get(),it)}) {
        properties {
            food(
                FoodProperties.Builder()
                    .fast()
                    .nutrition(1)
                    .saturationModifier(.5f)
                    .build()
            )
        }

        addData(Providers.LANG) { ctx ->
            add(ctx(),"Sturgeon caviar")
        }
        addData(Providers.ITEM_MODELS) { ctx ->
            generated(ctx)
        }

        tab(tab)
    }

    val buterbrod = registrate.item("sturgeon_caviar_sandwich",::Item) {
        properties {
            food(
                FoodProperties.Builder()
                    .fast()
                    .nutrition(4)
                    .saturationModifier(1f)
                    .build()
            )
        }

        addData(Providers.LANG) { ctx ->
            add(ctx(),"Sturgeon Caviar Sandwich")
        }
        addData(Providers.ITEM_MODELS) { ctx ->
            generated(ctx)
        }
        addData(Providers.RECIPE) { ctx ->
            ShapedRecipeBuilder.shaped(
                RecipeCategory.FOOD,
                ctx()
            )
                .define('c',sturgeonCaviarItem.get())
                .define('b', Items.BREAD)
                .pattern("c")
                .pattern("b")
                .unlockedBy(
                    "criterion", Criterion(
                        CriteriaTriggers.IMPOSSIBLE,
                        ImpossibleTrigger.TriggerInstance()
                    )
                )
                .save(
                    this
                )
        }

        tab(tab)
    }

    val cookedSturgeon = registrate.item("cooked_sturgeon",::Item) {
        properties {
            food(
                FoodProperties.Builder()
                    .fast()
                    .nutrition(6)
                    .saturationModifier(1.2f)
                    .build()
            )
        }

        addData(Providers.LANG) { ctx ->
            add(ctx(),"Cooked Sturgeon")
        }
        addData(Providers.ITEM_MODELS) { ctx ->
            generated(ctx)
        }
        addData(Providers.RECIPE) { ctx ->
            cooking(
                DataIngredient.items(sturgeonItem()),
                RecipeCategory.FOOD,
                ctx,
                0.35f, 200,
                RecipeSerializer.SMELTING_RECIPE,
                ::SmeltingRecipe
            )
        }

        tab(tab)
    }


    val AJIGFOJUSRGJOISRGJNDHFJBGHDSYFBSDRNVGDRFSGBVDRFG: JsonObject = Gson()
        .fromJson(
            """
                          {
                           "thirdperson_righthand": {
                                        "rotation": [ 0, -90, 55 ],
                                        "translation": [ 0, 4.0, 0.5 ],
                                        "scale": [ 0.85, 0.85, 0.85 ]
                                    },
                                    "thirdperson_lefthand": {
                                        "rotation": [ 0, 90, -55 ],
                                        "translation": [ 0, 4.0, 0.5 ],
                                        "scale": [ 0.85, 0.85, 0.85 ]
                                    },
                                    "firstperson_righthand": {
                                        "rotation": [ 0, -90, 25 ],
                                        "translation": [ 1.13, 3.2, 1.13 ],
                                        "scale": [ 0.68, 0.68, 0.68 ]
                                    },
                                    "firstperson_lefthand": {
                                        "rotation": [ 0, 90, -25 ],
                                        "translation": [ 1.13, 3.2, 1.13 ],
                                        "scale": [ 0.68, 0.68, 0.68 ]
                                    }
                          }
                     
                     """.trimIndent(),
            JsonObject::class.java
        )

    val swordfish = registrate.item(
        "swordfish_item",
        { SwordItem(Tiers.STONE,it) }
    ) {
        properties {
            food(
                FoodProperties.Builder()
                    .nutrition(2)
                    .saturationModifier(2f)
                    .build()
            )
            attributes(SwordItem.createAttributes(Tiers.STONE, 3, -2.4f))
            durability(50)
            stacksTo(1)
            setNoRepair()
        }

        addData(Providers.LANG) { ctx ->
            add(ctx(),"Swordfish")
        }

        tab(tab)

        addData(Providers.ITEM_MODELS) {
            val guiModel: ItemModelBuilder? = nested()
                .parent(getExistingFile(mcLoc("item/generated")))
                .texture("layer0", modLoc("item/swordfish_item"))
                .texture("particle", modLoc("item/swordfish_item"))
            val scale = .5f

            val handModel =
                nested()
                    .parent(getExistingFile(mcLoc("item/handheld")))
                    .texture("layer0", modLoc("item/swordfish_hand"))
                    .texture("particle", modLoc("item/swordfish_item"))
                    .transforms()

            for (view in ItemDisplayContext.entries) {
                val trans =
                    handModel.transform(view)
                val h: JsonElement? = AJIGFOJUSRGJOISRGJNDHFJBGHDSYFBSDRNVGDRFSGBVDRFG.get(view.getSerializedName())
                if (h != null) {
                    val translation = h.getAsJsonObject().get("translation").getAsJsonArray()
                    val scales = h.getAsJsonObject().get("scale").getAsJsonArray()
                    val rot = h.getAsJsonObject().get("rotation").getAsJsonArray()
                    trans.translation(
                        translation.get(0).getAsFloat(),
                        translation.get(1).getAsFloat(),
                        translation.get(2).getAsFloat()
                    )
                    val g = if (view == ItemDisplayContext.GROUND) 1.5f else 0f
                    trans.scale(
                        scales.get(0).getAsFloat() + scale + g,
                        scales.get(1).getAsFloat() + scale + g,
                        scales.get(2).getAsFloat() + scale + g
                    )
                    trans.rotation(
                        rot.get(0).getAsFloat(),
                        rot.get(1).getAsFloat(),
                        rot.get(2).getAsFloat()
                    )
                }
                trans.end()
            }
            getBuilder("swordfish_item")
                .parent(getExistingFile(mcLoc("item/generated")))
                .customLoader({ parent, existingFileHelper ->
                    SeparateTransformsModelBuilder.begin(
                        parent,
                        existingFileHelper
                    )
                })
                .base(handModel.end())
                .perspective(ItemDisplayContext.GUI, guiModel)
                .perspective(ItemDisplayContext.FIXED, guiModel)
                .perspective(ItemDisplayContext.GROUND, guiModel)
                .end()
        }
    
        addData(Providers.ITEM_TAGS) { ctx -> add(ItemTags.FISHES).add(ctx()) }
    }

    val piranhaBucket = registrate.item(
        "piranha_bucket",
        { MobBucketItem(
            AquaticEntities.piranha.get(),
            Fluids.WATER,
            SoundEvents.BUCKET_EMPTY_FISH,
            it
        )}
    ) {
        properties {
            component(DataComponents.BUCKET_ENTITY_DATA, CustomData.EMPTY)
        }

        addData(Providers.LANG) { ctx ->
            add(ctx(),"Piranha in a Bucket")
        }
        addData(Providers.ITEM_MODELS) { ctx ->
            generated(ctx)
        }

        tab(tab)
    }

    val piranha = registrate.item("piranha_item",::Item) {
        properties {
            food(
                FoodProperties.Builder()
                    .nutrition(2)
                    .saturationModifier(2f)
                    .effect(MobEffectInstance(MobEffects.HUNGER, 600, 0), 0.8F)
                .build()
            )
        }
        addData(Providers.LANG) { ctx ->
            add(ctx(),"Piranha")
        }
        addData(Providers.ITEM_MODELS) { ctx ->
            generated(ctx)
        }

        tab(tab)
    
        addData(Providers.ITEM_TAGS) { ctx -> add(ItemTags.FISHES).add(ctx()) }
    }

    val piranhaArrow = registrate.item("piranha_arrow",::PiranhaArrowItem) {
        onRegister {
            DispenserBlock.registerBehavior(
                it, ProjectileDispenseBehavior(it)
            )
        }
        addData(Providers.RECIPE) { ctx ->
            ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, ctx.get())
                .requires(Items.ARROW)
                .requires(piranha)
                .unlockedBy(
                    "criterion", Criterion(
                        CriteriaTriggers.IMPOSSIBLE,
                        ImpossibleTrigger.TriggerInstance()
                    )
                )
                .save(this, loc("piranha_arrow"))
        }
        addData(Providers.LANG) { ctx ->
            add(ctx(),"Piranha Arrow")
        }
        addData(Providers.ITEM_MODELS) { ctx ->
            generated(ctx)
        }
        addData(Providers.ITEM_TAGS) { ctx ->
            add(ItemTags.ARROWS).add(ctx())
        }

        tab(tab)
    }
}