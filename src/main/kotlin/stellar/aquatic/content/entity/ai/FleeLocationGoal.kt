package stellar.aquatic.content.entity.ai

import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.util.DefaultRandomPos
import net.minecraft.world.level.pathfinder.Path
import net.minecraft.world.phys.Vec3
import java.util.EnumSet
import java.util.function.*

class FleeLocationGoal(
    val mob: PathfinderMob,
    val locationSupplier: Supplier<Vec3?>,
    val condition: Predicate<PathfinderMob?>,
    val speedModifier: Double,
    dangerRadius: Float,
    safeRadius: Float
) : Goal() {
    val dangerRadiusSqr: Double = (dangerRadius * dangerRadius).toDouble()
    val safeRadiusSqr: Double = (safeRadius * safeRadius).toDouble()

    var path: Path? = null
    var dangerLoc: Vec3? = null

    init {
        this.flags = EnumSet.of(Flag.MOVE)
    }

    override fun canUse(): Boolean {
        if (! this.condition.test(this.mob)) {
            return false
        }

        this.dangerLoc = this.locationSupplier.get()
        if (this.dangerLoc == null) {
            return false
        }

        if (this.mob.distanceToSqr(this.dangerLoc) < this.dangerRadiusSqr) {
            val escapePos = this.findEscapePosition() ?: return false

            this.path = this.mob.getNavigation().createPath(escapePos.x, escapePos.y, escapePos.z, 0)
            return this.path != null
        }

        return false
    }

    override fun canContinueToUse(): Boolean {
        if (this.dangerLoc == null) {
            return false
        }

        val distanceSqr = this.mob.distanceToSqr(this.dangerLoc)
        return distanceSqr < this.safeRadiusSqr && ! this.mob.getNavigation().isDone
    }

    override fun start() {
        this.mob.getNavigation().moveTo(this.path, this.speedModifier)
    }

    override fun stop() {
        this.mob.getNavigation().stop()
        this.path = null
        this.dangerLoc = null
    }

    override fun tick() {
        if (this.mob.getNavigation().isDone && this.dangerLoc != null) {
            if (this.mob.distanceToSqr(this.dangerLoc) < this.safeRadiusSqr) {
                val escapePos = this.findEscapePosition()
                if (escapePos != null) {
                    this.mob.getNavigation().moveTo(escapePos.x, escapePos.y, escapePos.z, this.speedModifier)
                }
            }
        }
    }

    private fun findEscapePosition(): Vec3? {
        return DefaultRandomPos.getPosAway(this.mob, 16, 7, this.dangerLoc)
    }
}