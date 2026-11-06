package stellar.aquatic.content.entity

import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.animal.Animal
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelReader

abstract class AquaticAnimal(
    type: EntityType<out AquaticAnimal>,
    level: Level
) : Animal(type, level) {

    companion object {
        const val MAX_AIR_SUPPLY = 300
        const val DROWN_AIR_THRESHOLD = -20
        const val DROWN_DAMAGE = 2.0f
        const val BASE_AMBIENT_SOUND_INTERVAL = 120
    }

    override fun checkSpawnObstruction(level: LevelReader): Boolean = level.isUnobstructed(this)

    override fun getAmbientSoundInterval(): Int = BASE_AMBIENT_SOUND_INTERVAL

    override fun getBaseExperienceReward(): Int = 1 + random.nextInt(3)

    protected open fun handleAirSupply(currentAir: Int) {
        if (isAlive && !isInWaterOrBubble) {
            val nextAir = currentAir - 1
            airSupply = nextAir
            if (nextAir == DROWN_AIR_THRESHOLD) {
                airSupply = 0
                hurt(damageSources().drown(), DROWN_DAMAGE)
            }
        } else {
            airSupply = MAX_AIR_SUPPLY
        }
    }

    override fun baseTick() {
        val currentAir = airSupply
        super.baseTick()
        handleAirSupply(currentAir)
    }

    override fun isPushedByFluid(): Boolean = false

    override fun canBeLeashed(): Boolean = false
}