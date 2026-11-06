package stellar.aquatic.content.entity.swordfish

import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.AnimationState
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MoverType
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal
import net.minecraft.world.entity.EntitySelector
import net.minecraft.world.entity.ai.util.DefaultRandomPos
import net.minecraft.core.BlockPos
import net.minecraft.world.level.pathfinder.PathComputationType
import net.minecraft.world.entity.animal.AbstractFish
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.pathfinder.PathType
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.network.PacketDistributor
import stellar.aquatic.content.entity.ai.FleeLocationGoal
import stellar.aquatic.content.network.S2CSwordfishBoostStart
import stellar.aquatic.index.AquaticAttachments
import stellar.aquatic.index.AquaticTags
import stellar.ether.api.EntityRef
import stellar.ether.api.rendering.lerp.ExponentialResolver
import stellar.ether.api.rendering.lerp.Lerp
import kotlin.math.max

class SwordfishEntity(
    type: EntityType<out SwordfishEntity>,
    level: Level
) : AbstractFish(
    type,
    level
){
    companion object {
        val IS_RIDEN = SynchedEntityData.defineId(
            SwordfishEntity::class.java,
            EntityDataSerializers.BOOLEAN
        )
        val WATER_TICKS = SynchedEntityData.defineId(
            SwordfishEntity::class.java,
            EntityDataSerializers.INT
        )
    }

    init {
        this.setPathfindingMalus(PathType.WATER, 0.0f)
        this.setPathfindingMalus(PathType.WALKABLE, -1.0f)
        this.setPathfindingMalus(PathType.OPEN, -1.0f)
        this.moveControl = SmoothSwimmingMoveControl(this, 85, 10, 0.02f, 0.1f, true)
        this.lookControl = SmoothSwimmingLookControl(this, 10)
    }

    val flop = AnimationState()
    val swim = AnimationState()

    val clientPitch = Lerp(ExponentialResolver(.2f))
    val rider = EntityRef(Player::class.java)
    val lastRider = EntityRef(Player::class.java)
    var runTime = 0

    override fun defineSynchedData(builder: SynchedEntityData.Builder) {
        super.defineSynchedData(builder)
        builder.define(IS_RIDEN,false)
        builder.define(WATER_TICKS, 0);
    }

    override fun registerGoals() {
        goalSelector.addGoal(0, net.minecraft.world.entity.ai.goal.PanicGoal(this, 1.8))
        goalSelector.addGoal(2, AvoidEntityGoal(this, Player::class.java, 8.0f, 1.6, 1.4, EntitySelector.NO_SPECTATORS::test))
        goalSelector.addGoal(1, FleeLocationGoal(this, { lastRider.get(level())?.position() }, { _ -> runTime != 0}, 1.5, 24f, 32f))
        goalSelector.addGoal(4, object : RandomSwimmingGoal(this,1.0,40) {
            override fun canUse(): Boolean {
                return canRandomSwim() && super.canUse()
            }
        })
        goalSelector.addGoal(6, object : RandomSwimmingGoal(this, 1.0, 5) {
            override fun getPosition(): Vec3? {
                var vec3 = DefaultRandomPos.getPos(mob, 15, 15)
                var i = 0
                while (vec3 != null && !mob.level().getBlockState(BlockPos.containing(vec3)).isPathfindable(PathComputationType.WATER) && i++ < 10) {
                    vec3 = DefaultRandomPos.getPos(mob, 15, 15)
                }
                return vec3
            }
        })
    }

    fun canDismountRider(): Boolean = entityData[WATER_TICKS] == 0

    override fun baseTick() {
        val air = airSupply
        super.baseTick()
        if (isAlive && !isInWaterOrBubble) {
            airSupply = air - 1
            if (airSupply == -20) { airSupply = 0; hurt(damageSources().drown(), 2.0f) }
        } else { airSupply = 300 }
    }

    override fun tick() {
        super.tick()

        runTime = max(runTime-1, 0)

        entityData[WATER_TICKS] = (this.getEntityData().get(WATER_TICKS) - 1).coerceAtLeast(0)

        if(this.isInWater) {
            entityData[WATER_TICKS] = 80
            flop.stop()
            swim.startIfStopped(tickCount)
        } else {
            swim.stop()
            flop.startIfStopped(tickCount)
        }

        entityData[IS_RIDEN] = rider.isPresent()

        if (!level().isClientSide && rider.isPresent() && this.isInWater) {
            val riderEntity = rider.get(level())!!

            this.setRot(riderEntity.yRot, riderEntity.xRot)
            this.move(
                MoverType.PLAYER,
                riderEntity.lookAngle.scale(this.getAttributeValue(Attributes.MOVEMENT_SPEED) / 2.15)
            )
        }
    }

    override fun isEffectiveAi(): Boolean = super.isEffectiveAi && !rider.isPresent()

    override fun travel(travelVector: Vec3) {
        if (this.isEffectiveAi && this.isInWater) {
            this.moveRelative(if(rider.isPresent() || runTime != 0) .035f else .01f, travelVector)
            this.move(MoverType.SELF, this.deltaMovement)
            this.deltaMovement = this.deltaMovement.scale(0.9)
            if (this.target == null) {
                this.deltaMovement = this.deltaMovement.add(0.0, -0.005, 0.0)
            }
        } else {
            super.travel(travelVector)
        }
    }

    override fun getMaxSpawnClusterSize(): Int = 8

    override fun getFlopSound(): SoundEvent = SoundEvents.SALMON_FLOP

    override fun getBucketItemStack(): ItemStack = Items.AIR.defaultInstance

    override fun mobInteract(player: Player, hand: InteractionHand): InteractionResult {
        val item = player.getItemInHand(hand)
        if(
            !level().isClientSide
            && item.`is`(AquaticTags.swordfish_consumable)
            && !rider.isPresent()
            && !player.getData(AquaticAttachments.player_riding_swordfish)
            && runTime == 0
        ) {
            if(!player.hasInfiniteMaterials()) {
                player.setItemInHand(hand, Items.WATER_BUCKET.defaultInstance)
            }
            PacketDistributor.sendToPlayer(
                player as ServerPlayer,
                S2CSwordfishBoostStart(id)
            )
            lastRider.set(null)
            rider.set(player)
            player.setData(AquaticAttachments.player_riding_swordfish,true)
            player.forcedPose = net.minecraft.world.entity.Pose.SWIMMING
            this.level().playSound(
                this,
                this.onPos,
                SoundEvents.GENERIC_EAT,
                SoundSource.AMBIENT,
                1f, 1f
            )
            return InteractionResult.SUCCESS
        }
        return InteractionResult.FAIL
    }
}