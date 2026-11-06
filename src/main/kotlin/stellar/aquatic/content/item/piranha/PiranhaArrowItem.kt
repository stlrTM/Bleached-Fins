package stellar.aquatic.content.item.piranha

import net.minecraft.core.*
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.projectile.*
import net.minecraft.world.item.*
import net.minecraft.world.level.Level
import stellar.aquatic.content.entity.piranha.arrow.PiranhaArrowEntity

class PiranhaArrowItem(p_43235_: Properties) : ArrowItem(p_43235_) {
    override fun createArrow(
        p_43237_: Level,
        p_43238_: ItemStack,
        p_43239_: LivingEntity,
        p_345773_: ItemStack?
    ): AbstractArrow {
        return PiranhaArrowEntity(p_43237_, p_43239_, p_43238_.copyWithCount(1), p_345773_)
    }

    override fun asProjectile(
        p_338332_: Level,
        p_338313_: Position,
        p_338304_: ItemStack,
        p_338842_: Direction
    ): Projectile {
        val spectralarrow = PiranhaArrowEntity(
            p_338332_,
            p_338313_.x(),
            p_338313_.y(),
            p_338313_.z(),
            p_338304_.copyWithCount(1),
            null as ItemStack?
        )
        return spectralarrow
    }
}
