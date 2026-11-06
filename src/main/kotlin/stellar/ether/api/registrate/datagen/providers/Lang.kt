package stellar.ether.api.registrate.datagen.providers

import net.neoforged.fml.LogicalSide
import net.neoforged.neoforge.common.data.LanguageProvider
import stellar.ether.api.registrate.datagen.Context
import stellar.ether.api.registrate.datagen.RegistrateProvider

class Lang(
    override val ctx: Context<Lang>
) : LanguageProvider(ctx.output,ctx.parent.modId,"en_us"), RegistrateProvider {
    override fun addTranslations() {
        ctx.genData(this)
    }

    override fun side(): LogicalSide = LogicalSide.CLIENT
}