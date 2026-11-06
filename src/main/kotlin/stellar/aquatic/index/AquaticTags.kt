package stellar.aquatic.index

import net.minecraft.core.registries.Registries
import net.minecraft.data.tags.ItemTagsProvider
import net.minecraft.tags.TagKey
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.data.event.GatherDataEvent
import stellar.aquatic.loc
import stellar.ether.api.Loadable

object AquaticTags : Loadable {
    val swordfish_consumable = TagKey.create(
        Registries.ITEM,
        loc("swordfish_consumables")
    )
}