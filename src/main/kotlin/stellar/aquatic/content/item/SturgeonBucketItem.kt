package stellar.aquatic.content.item

import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundEvent
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.*
import net.minecraft.world.item.component.CustomModelData
import net.minecraft.world.level.material.Fluid

class SturgeonBucketItem(
    type: EntityType<*>,
    fluid: Fluid,
    empty: SoundEvent,
    props: Properties
) : MobBucketItem(
    type,
    fluid,
    empty,
    props
) {
    override fun getName(stack: ItemStack): Component {
        return Component.translatable(this.getDescriptionId(stack) + if(stack.getOrDefault(DataComponents.CUSTOM_MODEL_DATA, CustomModelData.DEFAULT).value == 1) {
            "_baby"
        } else {
            ""
        })
    }
}