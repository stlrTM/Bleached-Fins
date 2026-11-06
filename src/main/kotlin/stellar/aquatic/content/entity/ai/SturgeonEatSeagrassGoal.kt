package stellar.aquatic.content.entity.ai

import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.Items
import stellar.aquatic.content.entity.sturgeon.SturgeonEntity
import kotlin.jvm.optionals.getOrNull
import java.util.EnumSet

class SturgeonEatSeagrassGoal(
    val entity: SturgeonEntity,
    val speedModifier: Float,
    val searchRadius: Float
) : Goal() {
    init { flags = EnumSet.of(Flag.MOVE, Flag.LOOK) }
    private var targetItem: ItemEntity? = null
    
    override fun canUse(): Boolean {

        if (entity.isBaby || entity.age != 0 || entity.isInLove) {
            return false
        }

        val searchBox = entity.boundingBox.inflate(this.searchRadius.toDouble(), 6.0, this.searchRadius.toDouble())
        val items = entity.level().getEntitiesOfClass(
            ItemEntity::class.java,
            searchBox,
            { item ->
                item!!.isAlive && item.item.`is`(Items.SEAGRASS) && item.isInWater
            }
        )

        return items.isNotEmpty() && items.stream()
            .min { item1, item2 ->
                entity.distanceToSqr(item1).compareTo(entity.distanceToSqr(item2))
            }.let {
                targetItem = it.getOrNull()
                return@let targetItem != null
            }
    }

    override fun canContinueToUse(): Boolean {
        if (this.targetItem == null || !this.targetItem!!.isAlive || !this.targetItem!!.isInWater) {
            return false
        }
        return !entity.isBaby && entity.getAge() == 0 && !entity.isInLove
    }

    override fun start() {
        entity.getNavigation().moveTo(this.targetItem, this.speedModifier.toDouble())
    }

    override fun stop() {
        this.targetItem = null
        entity.getNavigation().stop()
    }

    override fun tick() {
        if (this.targetItem == null) return

        entity.getLookControl().setLookAt(this.targetItem!!, 30.0f, 30.0f)

        if (entity.getRandom().nextInt(10) == 0) {
            entity.getNavigation().moveTo(this.targetItem!!, this.speedModifier.toDouble())
        }

        if (entity.distanceToSqr(this.targetItem!!) < 2.25) {
            this.eatItem()
        }
    }

    private fun eatItem() {
        if (this.targetItem != null && this.targetItem!!.isAlive()) {
            this.targetItem!!.item.shrink(1)

            entity.level().playSound(
                null,
                entity.x, entity.y, entity.z,
                SoundEvents.GENERIC_EAT,
                SoundSource.NEUTRAL,
                0.7f, 1.2f + entity.getRandom().nextFloat() * 0.2f
            )

            entity.level().broadcastEntityEvent(entity, 18.toByte())
            entity.setInLove(null)

            this.stop()
        }
    }
}