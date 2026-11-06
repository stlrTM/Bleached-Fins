package stellar.aquatic.content.entity.ai

import net.minecraft.world.entity.ai.goal.Goal
import stellar.aquatic.content.entity.AquaticSchoolingFish

class FollowFlockLeaderGoal(private val mob: AquaticSchoolingFish) : Goal() {

    private var timeToRecalcPath = 0
    private var nextStartTick: Int = calculateNextStartTick(mob)

    private fun calculateNextStartTick(taskOwner: AquaticSchoolingFish): Int {
        return reducedTickDelay(BASE_RECALC_INTERVAL + taskOwner.random.nextInt(200) % 20)
    }

    override fun canUse(): Boolean {
        if (mob.hasFollowers()) return false
        if (mob.isFollower()) return true

        if (nextStartTick > 0) {
            nextStartTick--
            return false
        }

        nextStartTick = calculateNextStartTick(mob)
        val searchBox = mob.boundingBox.inflate(SEARCH_RADIUS)
        val nearbyFish = mob.level().getEntitiesOfClass(
            mob::class.java,
            searchBox
        ) { entity -> entity.canBeFollowed() || !entity.isFollower() }

        val leaderCandidate = nearbyFish.firstOrNull { it.canBeFollowed() } ?: mob
        leaderCandidate.addFollowers(nearbyFish.asSequence().filter { !it.isFollower() })

        return mob.isFollower()
    }

    override fun canContinueToUse(): Boolean {
        return mob.isFollower() && mob.inRangeOfLeader()
    }

    override fun start() {
        timeToRecalcPath = 0
    }

    override fun stop() {
        mob.stopFollowing()
    }

    override fun tick() {
        if (--timeToRecalcPath <= 0) {
            timeToRecalcPath = adjustedTickDelay(PATH_RECALC_INTERVAL)
            mob.pathToLeader()
        }
    }

    companion object {
        private const val SEARCH_RADIUS = 8.0
        private const val BASE_RECALC_INTERVAL = 200
        private const val PATH_RECALC_INTERVAL = 10
    }
}