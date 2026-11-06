package stellar.aquatic

import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.neoforged.fml.common.Mod
import stellar.aquatic.index.AquaticAttachments
import stellar.aquatic.index.AquaticBlockEntities
import stellar.aquatic.index.AquaticBlocks
import stellar.aquatic.index.AquaticEntities
import stellar.aquatic.index.AquaticItems
import stellar.aquatic.index.AquaticNetwork
import stellar.aquatic.index.AquaticSounds
import stellar.aquatic.index.AquaticTags
import stellar.ether.api.registrate.AbstractRegistrate
import stellar.ether.api.registrate.builders.SimpleBuilders.creativeModeTab
import thedarkcolour.kotlinforforge.neoforge.KotlinModLoadingContext

class AquaticRegistrate : AbstractRegistrate<AquaticRegistrate>("ether_aquatic")

fun loc(id: String): ResourceLocation = ResourceLocation.fromNamespaceAndPath("ether_aquatic",id)

@Mod("ether_aquatic")
object Aquatic {
    val registrate = AquaticRegistrate()

    val tab = registrate.creativeModeTab(
        "tab",
        {
            title(Component.literal("Bleached Fins"))
        }
    )

    init {
        AquaticBlockEntities.load()
        AquaticTags.load()
        AquaticBlocks.load()
        AquaticAttachments.load()
        AquaticEntities.load()
        AquaticItems.load()
        AquaticSounds.load()
        registrate.register(KotlinModLoadingContext.get().getKEventBus())
    }
}