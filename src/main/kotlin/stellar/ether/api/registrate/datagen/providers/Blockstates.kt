package stellar.ether.api.registrate.datagen.providers

import net.minecraft.world.level.block.Block
import net.neoforged.fml.LogicalSide
import net.neoforged.neoforge.client.model.generators.*
import stellar.ether.api.registrate.datagen.Context
import stellar.ether.api.registrate.datagen.RegistrateProvider
import java.util.*

class Blockstates(
    override val ctx: Context<Blockstates>
) : BlockStateProvider(ctx.output,ctx.parent.modId,ctx.fileHelper), RegistrateProvider {
    override fun side(): LogicalSide = LogicalSide.CLIENT

    override fun registerStatesAndModels() {
        ctx.genData(this)
    }

    fun getExistingVariantBuilder(block: Block): Optional<VariantBlockStateBuilder> {
        return Optional.ofNullable(registeredBlocks[block])
            .filter { b -> b is VariantBlockStateBuilder }
            .map{ b -> b as VariantBlockStateBuilder }
    }

    fun getExistingMultipartBuilder(block: Block): Optional<MultiPartBlockStateBuilder> {
        return Optional.ofNullable(registeredBlocks[block])
            .filter { b -> b is MultiPartBlockStateBuilder }
            .map { b -> b as MultiPartBlockStateBuilder }
    }
}