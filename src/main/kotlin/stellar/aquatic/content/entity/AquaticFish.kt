package stellar.aquatic.content.entity

import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.tags.FluidTags
import net.minecraft.util.Mth
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.EntitySelector
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MoverType
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.control.MoveControl
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal
import net.minecraft.world.entity.ai.goal.PanicGoal
import net.minecraft.world.entity.ai.navigation.PathNavigation
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation
import net.minecraft.world.entity.animal.Bucketable
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.pathfinder.PathType
import net.minecraft.world.phys.Vec3
import stellar.aquatic.content.entity.ai.FishSwimGoal
import kotlin.math.sqrt

abstract class AquaticFish(
    type: EntityType<out AquaticFish>,
    level: Level
) : AquaticAnimal(type, level), Bucketable {

    companion object {
        val FROM_BUCKET: EntityDataAccessor<Boolean> = SynchedEntityData.defineId(
            AquaticFish::class.java,
            EntityDataSerializers.BOOLEAN
        )
        private const val NBT_FROM_BUCKET = "FromBucket"
        private const val DEFAULT_MAX_CLUSTER_SIZE = 8
    }

    init {
        this.setPathfindingMalus(PathType.WATER, 0.0f)
        this.setPathfindingMalus(PathType.WALKABLE, -1.0f)
        this.setPathfindingMalus(PathType.OPEN, -1.0f)
        this.moveControl = SmoothSwimmingMoveControl(this, 85, 10, 0.02f, 0.1f, true)
        this.lookControl = SmoothSwimmingLookControl(this, 10)
    }

    override fun requiresCustomPersistence(): Boolean = super.requiresCustomPersistence() || fromBucket()

    override fun removeWhenFarAway(distanceToClosestPlayer: Double): Boolean = !fromBucket() && !hasCustomName()

    override fun getMaxSpawnClusterSize(): Int = DEFAULT_MAX_CLUSTER_SIZE

    override fun defineSynchedData(builder: SynchedEntityData.Builder) {
        super.defineSynchedData(builder)
        builder.define(FROM_BUCKET, false)
    }

    override fun fromBucket(): Boolean = entityData.get(FROM_BUCKET)

    override fun setFromBucket(fromBucket: Boolean) {
        entityData.set(FROM_BUCKET, fromBucket)
    }

    override fun addAdditionalSaveData(compound: CompoundTag) {
        super.addAdditionalSaveData(compound)
        compound.putBoolean(NBT_FROM_BUCKET, fromBucket())
    }

    override fun readAdditionalSaveData(compound: CompoundTag) {
        super.readAdditionalSaveData(compound)
        setFromBucket(compound.getBoolean(NBT_FROM_BUCKET))
    }

    override fun registerGoals() {
        super.registerGoals()
        goalSelector.addGoal(0, PanicGoal(this, 1.25))
        goalSelector.addGoal(
            2,
            AvoidEntityGoal(
                this,
                Player::class.java,
                8.0f,
                1.6,
                1.4,
                EntitySelector.NO_SPECTATORS::test
            )
        )
        goalSelector.addGoal(4, FishSwimGoal(this))
    }

    override fun createNavigation(level: Level): PathNavigation = WaterBoundPathNavigation(this, level)

    override fun travel(travelVector: Vec3) {
        if (isEffectiveAi && isInWater) {
            moveRelative(0.01f, travelVector)
            move(MoverType.SELF, deltaMovement)
            deltaMovement = deltaMovement.scale(0.9)
            if (target == null) {
                deltaMovement = deltaMovement.add(0.0, -0.005, 0.0)
            }
        } else {
            super.travel(travelVector)
        }
    }

    override fun aiStep() {
        if (!isInWater && onGround() && verticalCollision) {
            val impulseX = Mth.nextDouble(random,-0.05, 0.05)
            val impulseZ = Mth.nextDouble(random,-0.05, 0.05)
            deltaMovement = deltaMovement.add(impulseX, 0.4, impulseZ)
            setOnGround(false)
            hasImpulse = true
            makeSound(getFlopSound())
        }
        super.aiStep()
    }

    override fun mobInteract(player: Player, hand: InteractionHand): InteractionResult {
        return Bucketable.bucketMobPickup(player, hand, this).orElseGet {
            super.mobInteract(player, hand)
        }
    }

    override fun saveToBucketTag(stack: ItemStack) {
        Bucketable.saveDefaultDataToBucketTag(this, stack)
    }

    override fun loadFromBucketTag(tag: CompoundTag) {
        Bucketable.loadDefaultDataFromBucketTag(this, tag)
    }

    override fun getPickupSound(): SoundEvent = SoundEvents.BUCKET_FILL_FISH
    open fun canRandomSwim(): Boolean = true
    protected abstract fun getFlopSound(): SoundEvent
    override fun getSwimSound(): SoundEvent = SoundEvents.FISH_SWIM
    override fun playStepSound(pos: BlockPos, block: BlockState) {}
}