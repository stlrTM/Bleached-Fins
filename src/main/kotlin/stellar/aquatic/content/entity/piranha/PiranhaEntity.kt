package stellar.aquatic.content.entity.piranha

import net.minecraft.nbt.CompoundTag
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.tags.FluidTags
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.AgeableMob
import net.minecraft.world.entity.AnimationState
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntitySelector
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.MoverType
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal
import net.minecraft.world.entity.ai.goal.PanicGoal
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import stellar.aquatic.content.entity.AquaticSchoolingFish
import stellar.aquatic.content.entity.ai.FollowFlockLeaderGoal
import stellar.aquatic.index.AquaticItems
import stellar.ether.api.rendering.lerp.ExponentialResolver
import stellar.ether.api.rendering.lerp.Lerp

class PiranhaEntity(
    type: EntityType<out PiranhaEntity>,
    level: Level
) : AquaticSchoolingFish(type, level) {

    companion object {
        val LEADER: EntityDataAccessor<Boolean> = SynchedEntityData.defineId(
            PiranhaEntity::class.java,
            EntityDataSerializers.BOOLEAN
        )
    }

    val flop = AnimationState()
    val swim = AnimationState()
    val flop2 = AnimationState()
    val bite = AnimationState()
    val open_jaw = AnimationState()

    val clientPitch = Lerp(ExponentialResolver(0.2f))

    var lastAttackTick = 0

    init {
        moveControl = SmoothSwimmingMoveControl(this, 85, 10, 0.2f, 0.1f, true)
        lookControl = SmoothSwimmingLookControl(this, 10)
    }

    override fun defineSynchedData(builder: SynchedEntityData.Builder) {
        super.defineSynchedData(builder)
        builder.define(LEADER, false)
    }

    override fun registerGoals() {
        goalSelector.addGoal(0, PanicGoal(this, 3.0))
        goalSelector.addGoal(2, MeleeAttackGoal(this, 3.0, true))
        goalSelector.addGoal(3, object : AvoidEntityGoal<Player>(
            this, Player::class.java, 8.0f, 3.0, 3.0, EntitySelector.NO_SPECTATORS::test
        ) {
            override fun canUse(): Boolean = !this@PiranhaEntity.isFollower() && super.canUse()
        })
        goalSelector.addGoal(4, FollowFlockLeaderGoal(this))
        goalSelector.addGoal(5, object : RandomSwimmingGoal(this, 1.0, 40) {
            override fun canUse(): Boolean = !this@PiranhaEntity.isFollower() && super.canUse()
        })

        targetSelector.addGoal(0, object : Goal() {
            override fun canUse(): Boolean = isFollower()
            override fun canContinueToUse(): Boolean = canUse()
            override fun tick() {
                target = leader?.target
            }
        })
        targetSelector.addGoal(1, object : NearestAttackableTargetGoal<LivingEntity>(
            this, LivingEntity::class.java, 10, true, false,
            { e -> e.health < e.maxHealth * 0.5f && canTarget(e) }
        ) {
            override fun canUse(): Boolean =
                !this@PiranhaEntity.isFollower() && schoolSize > 3 && super.canUse()
        })
    }

    private fun canTarget(e: LivingEntity): Boolean =
        e !is PiranhaEntity &&
                e.type != EntityType.ARMOR_STAND &&
                e.type != EntityType.WARDEN &&
                EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(e) &&
                !e.isInvulnerable &&
                e.isAlive &&
                !isAlliedTo(e)

    private fun setPackTarget(e: LivingEntity) {
        target = e
        if (isFollower()) leader?.target = e
    }

    override fun getMaxSpawnClusterSize(): Int = 8
    override fun getMaxSchoolSize(): Int = 24

    override fun travel(travelVector: Vec3) {
        if (isEffectiveAi && isInWater) {
            moveRelative(if(this.target != null) 0.025f else 0.01f, travelVector)
            move(MoverType.SELF, deltaMovement)
            val sink = when {
                target != null -> 0.0
                level().getFluidState(blockPosition().above()).`is`(FluidTags.WATER) -> -0.005
                else -> -0.03
            }
            deltaMovement = deltaMovement.scale(0.9).add(0.0, sink, 0.0)
        } else {
            super.travel(travelVector)
        }
    }

    override fun doHurtTarget(entity: Entity): Boolean {
        lastAttackTick = 0
        level().broadcastEntityEvent(this, 4.toByte())
        playSound(SoundEvents.FOX_BITE, 1.0f, voicePitch)
        return super.doHurtTarget(entity)
    }

    override fun hurt(source: DamageSource, amount: Float): Boolean {
        (source.entity as? LivingEntity)?.takeIf(::canTarget)?.let(::setPackTarget)
        return super.hurt(source, amount)
    }

    override fun tick() {
        super.tick()
        lastAttackTick++

        if (!level().isClientSide) {
            entityData.set(LEADER, isFollower())

            if (target == null && tickCount % 5 == 0) {
                level().getEntitiesOfClass(LivingEntity::class.java, boundingBox.inflate(16.0)) {
                    it.hurtTime > 0 && canTarget(it)
                }.firstOrNull()?.let(::setPackTarget)
            }
        }

        if (isInWater) {
            flop.stop()
            swim.startIfStopped(tickCount)
        } else {
            swim.stop()
            flop.startIfStopped(tickCount)
        }
    }

    override fun handleEntityEvent(id: Byte) {
        if (id == 4.toByte()) {
            lastAttackTick = 0
            bite.start(tickCount)
        } else {
            super.handleEntityEvent(id)
        }
    }

    override fun getBreedOffspring(level: ServerLevel, partner: AgeableMob): AgeableMob? = null
    override fun isFood(stack: ItemStack): Boolean = false
    override fun getBucketItemStack(): ItemStack = AquaticItems.piranhaBucket.create()

    override fun getAmbientSound(): SoundEvent = SoundEvents.SALMON_AMBIENT
    override fun getDeathSound(): SoundEvent = SoundEvents.SALMON_DEATH
    override fun getHurtSound(damageSource: DamageSource): SoundEvent = SoundEvents.SALMON_HURT
    override fun getFlopSound(): SoundEvent = SoundEvents.SALMON_FLOP
    override fun getSwimSound(): SoundEvent = SoundEvents.FISH_SWIM
}