package stellar.ether.api.registrate.datagen.providers

import net.minecraft.core.HolderLookup
import net.neoforged.fml.LogicalSide
import net.neoforged.neoforge.common.data.DataMapProvider
import stellar.ether.api.registrate.datagen.Context
import stellar.ether.api.registrate.datagen.RegistrateProvider

class DataMaps(
    override val ctx: Context<DataMaps>
) : DataMapProvider(ctx.output,ctx.provider), RegistrateProvider {
    override fun gather(provider: HolderLookup.Provider) {
        super.gather(provider)
    }
    override fun side(): LogicalSide = LogicalSide.CLIENT
}