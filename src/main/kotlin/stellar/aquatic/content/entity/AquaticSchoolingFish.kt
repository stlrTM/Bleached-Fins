package stellar.aquatic.content.entity

import net.minecraft.world.DifficultyInstance
import net.minecraft.world.entity.AgeableMob
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobSpawnType
import net.minecraft.world.entity.SpawnGroupData
import net.minecraft.world.level.Level
import net.minecraft.world.level.ServerLevelAccessor
import stellar.aquatic.content.entity.ai.FollowFlockLeaderGoal

abstract class AquaticSchoolingFish(
    type: EntityType<out AquaticSchoolingFish>,
    level: Level
) : AquaticFish(type, level) {

    var leader: AquaticSchoolingFish? = null
        private set

    var schoolSize: Int = 1
        private set

    override fun registerGoals() {
        super.registerGoals()
        goalSelector.addGoal(5, FollowFlockLeaderGoal(this))
    }

    override fun getMaxSpawnClusterSize(): Int = getMaxSchoolSize()

    open fun getMaxSchoolSize(): Int = super.getMaxSpawnClusterSize()

    override fun canRandomSwim(): Boolean = !isFollower()

    fun isFollower(): Boolean = leader?.isAlive == true

    fun startFollowing(newLeader: AquaticSchoolingFish): AquaticSchoolingFish {
        this.leader = newLeader
        newLeader.addFollower()
        return newLeader
    }

    fun stopFollowing() {
        leader?.removeFollower()
        leader = null
    }

    private fun addFollower() {
        schoolSize++
    }

    private fun removeFollower() {
        schoolSize--
    }

    fun canBeFollowed(): Boolean = hasFollowers() && schoolSize < getMaxSchoolSize()

    fun hasFollowers(): Boolean = schoolSize > 1

    fun inRangeOfLeader(): Boolean {
        val currentLeader = leader ?: return false
        return distanceToSqr(currentLeader) <= MAX_LEADER_DISTANCE_SQ
    }

    fun pathToLeader() {
        if (isFollower()) {
            navigation.moveTo(leader, 1.0)
        }
    }

    fun addFollowers(followers: Sequence<AquaticSchoolingFish>) {
        val availableCapacity = getMaxSchoolSize() - schoolSize
        followers
            .filter { it !== this }
            .take(availableCapacity)
            .forEach { it.startFollowing(this) }
    }

    override fun tick() {
        super.tick()
        // Periodically recalculate school size if nearby entity count drops
        if (hasFollowers() && level().random.nextInt(200) == 1) {
            val nearbySchoolFish = level().getEntitiesOfClass(
                this::class.java,
                boundingBox.inflate(8.0, 8.0, 8.0)
            )
            if (nearbySchoolFish.size <= 1) {
                schoolSize = 1
            }
        }
    }

    override fun finalizeSpawn(
        level: ServerLevelAccessor,
        difficulty: DifficultyInstance,
        reason: MobSpawnType,
        spawnData: SpawnGroupData?
    ): SpawnGroupData? {
        var currentData = super.finalizeSpawn(level, difficulty, reason, spawnData)
        if (currentData is SchoolSpawnGroupData) {
            startFollowing(currentData.leader)
        } else {
            currentData = SchoolSpawnGroupData(this)
        }
        return currentData
    }

    companion object {
        private const val MAX_LEADER_DISTANCE_SQ = 11*11
    }

    class SchoolSpawnGroupData(
        val leader: AquaticSchoolingFish
    ) : AgeableMobGroupData(
        .2f
    )
}