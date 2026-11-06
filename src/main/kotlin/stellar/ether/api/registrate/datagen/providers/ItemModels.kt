package stellar.ether.api.registrate.datagen.providers

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.ItemLike
import net.neoforged.fml.LogicalSide
import net.neoforged.neoforge.client.model.generators.*
import net.neoforged.neoforge.client.model.generators.ModelFile.UncheckedModelFile
import stellar.ether.api.registrate.datagen.Context
import stellar.ether.api.registrate.datagen.RegistrateProvider

class ItemModels(
    override val ctx: Context<ItemModels>
) : ItemModelProvider(ctx.output,ctx.parent.modId,ctx.fileHelper), RegistrateProvider {

    override fun side(): LogicalSide = LogicalSide.CLIENT

    override fun registerModels() {
        ctx.genData(this)
    }

    fun modid(item: () -> ItemLike): String {
        return BuiltInRegistries.ITEM.getKey(item().asItem()).namespace
    }

    fun name(item: () -> ItemLike): String {
        return BuiltInRegistries.ITEM.getKey(item().asItem()).path
    }

    fun itemTexture(item: () -> ItemLike): ResourceLocation {
        return modLoc("item/" + name(item))
    }

    fun blockItem(block: () -> ItemLike): ItemModelBuilder {
        return blockItem(block, "")
    }

    fun blockItem(block: () -> ItemLike, suffix: String?): ItemModelBuilder {
        return withExistingParent(
            name(block),
            ResourceLocation.fromNamespaceAndPath(modid(block), "block/" + name(block) + suffix)
        )
    }

    fun blockWithInventoryModel(block: () -> ItemLike): ItemModelBuilder {
        return withExistingParent(
            name(block),
            ResourceLocation.fromNamespaceAndPath(modid(block), "block/" + name(block) + "_inventory")
        )
    }

    fun blockSprite(block: () -> ItemLike): ItemModelBuilder {
        return blockSprite(block, modLoc("block/" + name(block)))
    }

    fun blockSprite(block: () -> ItemLike, texture: ResourceLocation?): ItemModelBuilder {
        return generated({ block().asItem() }, texture)
    }

    fun generated(item: () -> ItemLike): ItemModelBuilder {
        return generated(item, itemTexture(item))
    }

    fun generated(item: () -> ItemLike, vararg layers: ResourceLocation?): ItemModelBuilder {
        var ret = getBuilder(name(item)).parent(UncheckedModelFile("item/generated"))
        for (i in layers.indices) {
            ret = ret.texture("layer$i", layers[i]!!)
        }
        return ret
    }

    fun handheld(item: () -> ItemLike): ItemModelBuilder {
        return handheld(item, itemTexture(item))
    }

    fun handheld(item: () -> ItemLike, texture: ResourceLocation): ItemModelBuilder {
        return withExistingParent(name(item), "item/handheld").texture("layer0", texture)
    }
}