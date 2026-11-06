package stellar.ether.api.registrate.datagen.providers

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.block.Block
import net.neoforged.fml.LogicalSide
import net.neoforged.neoforge.client.model.generators.*
import stellar.ether.api.registrate.datagen.Context
import stellar.ether.api.registrate.datagen.RegistrateProvider

class BlockModels(
    override val ctx: Context<BlockModels>
) : BlockModelProvider(ctx.output,ctx.parent.modId,ctx.fileHelper), RegistrateProvider {

    override fun side(): LogicalSide = LogicalSide.CLIENT

    override fun registerModels() {
        ctx.genData(this)
    }

    fun modid(item: () -> Block): String {
        return BuiltInRegistries.BLOCK.getKey(item()).namespace
    }

    fun name(item: () -> Block): String {
        return BuiltInRegistries.BLOCK.getKey(item()).path
    }
}