package stellar.aquatic.content.entity.sturgeon

import net.minecraft.advancements.CriteriaTriggers
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.stats.Stats
import net.minecraft.world.entity.AgeableMob
import net.minecraft.world.entity.AnimationState
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.ExperienceOrb
import net.minecraft.world.entity.VariantHolder
import net.minecraft.world.entity.ai.goal.*
import net.minecraft.world.entity.ai.util.DefaultRandomPos
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.EntitySelector
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl
import net.minecraft.world.level.storage.loot.LootParams
import net.minecraft.world.entity.animal.Animal
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.CustomData
import net.minecraft.world.item.component.CustomModelData
import net.minecraft.world.level.GameRules
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.level.pathfinder.PathComputationType
import net.minecraft.world.phys.Vec3
import stellar.aquatic.content.entity.AquaticSchoolingFish
import stellar.aquatic.content.entity.ai.SturgeonEatSeagrassGoal
import stellar.aquatic.content.entity.ai.FollowFlockLeaderGoal
import stellar.aquatic.index.AquaticAttachments
import stellar.aquatic.index.AquaticBlocks
import stellar.aquatic.index.AquaticItems
import stellar.aquatic.index.AquaticEntities
import stellar.ether.ether
import stellar.ether.api.NbtExtensions.set
import stellar.ether.api.rendering.lerp.ExponentialResolver
import stellar.ether.api.rendering.lerp.Lerp

class SturgeonEntity(
    type: EntityType<SturgeonEntity>,
    level: Level
) : AquaticSchoolingFish(type, level), VariantHolder<SturgeonVariant> {
    companion object {
        fun parse(str: String): ResourceLocation = ResourceLocation.tryParse(str)!!
        val biomesToVariant = mapOf(
            listOf(
                "minecraft:ocean",
                "minecraft:cold_ocean",
                "minecraft:deep_frozen_ocean",
                "minecraft:deep_cold_ocean",
                "minecraft:deep_ocean",
                "minecraft:lukewarm_ocean",
                "minecraft:deep_lukewarm_ocean"
            ).map(::parse) to SturgeonVariant.SEA,
            listOf(
                "minecraft:swamp",
                "environmental:marsh",
                "nomansland:bog",
                "nomansland:blackwater_river"
            ).map(::parse) to SturgeonVariant.SWAMP,
            listOf(
                "minecraft:river",
                "minecraft:mangrove_swamp",
                "nomansland:bayou"
            ).map(::parse) to SturgeonVariant.GREEN
        )
    }
    init {
        moveControl = SmoothSwimmingMoveControl(this, 85, 10, 0.02f, 0.1f, true)
        lookControl = SmoothSwimmingLookControl(this, 10)
    }

    val flop = AnimationState()
    val swim = AnimationState()
    val flop2 = AnimationState()

    val clientPitch = Lerp(
        ExponentialResolver(.2f)
    )

    override fun tick() {
        super.tick()

        if (this.isInWater) {
            flop.stop()
            swim.startIfStopped(tickCount)
        } else {
            swim.stop()
            flop.startIfStopped(tickCount)
        }
    }

    override fun onAddedToLevel() {
        super.onAddedToLevel()
        val current = this.level().getBiome(this.onPos)
        for ((key, value) in biomesToVariant) {
            for (biome in key) {
                if(current.`is`(biome)) {
                    variant = value
                    return
                }
            }
        }
    }

    override fun registerGoals() {
        goalSelector.addGoal(1, TemptGoal(this, 1.5, { stack -> stack.`is`(Items.SEAGRASS) }, true))
        goalSelector.addGoal(0, PanicGoal(this, 1.8))
        goalSelector.addGoal(2, AvoidEntityGoal(this, Player::class.java, 8.0f, 1.6, 1.4, EntitySelector.NO_SPECTATORS::test))
        goalSelector.addGoal(3, BreedGoal(this, 1.0))
        goalSelector.addGoal(4, SturgeonEatSeagrassGoal(this, 1.0f, 16.0f))
        goalSelector.addGoal(5, FollowParentGoal(this, 1.0))
        goalSelector.addGoal(6, object : RandomSwimmingGoal(this, 1.0, 5) {
            override fun canUse(): Boolean = !this@SturgeonEntity.isFollower() && super.canUse()
            override fun getPosition(): Vec3? {
                var vec3 = DefaultRandomPos.getPos(mob, 15, 15)
                var i = 0
                while (vec3 != null && !mob.level().getBlockState(BlockPos.containing(vec3)).isPathfindable(PathComputationType.WATER) && i++ < 10) {
                    vec3 = DefaultRandomPos.getPos(mob, 15, 15)
                }
                return vec3
            }
        })
        goalSelector.addGoal(7, FollowFlockLeaderGoal(this))
    }

    override fun getFlopSound(): SoundEvent = SoundEvents.SALMON_FLOP
    override fun isFood(p0: ItemStack): Boolean = p0.`is`(Items.COD) || p0.`is`(Items.SALMON) || p0.`is`(Items.TROPICAL_FISH)
    override fun getBreedOffspring(p0: ServerLevel, p1: AgeableMob): AgeableMob? {
        val offspring = AquaticEntities.sturgeon.get().create(p0)
        if (offspring != null) offspring.setFromBucket(true)
        return offspring
    }

    override fun getBucketItemStack(): ItemStack {
        val stack = AquaticItems.sturgeonBucket.create()
        if(isBaby) {
            stack.set(DataComponents.CUSTOM_MODEL_DATA, CustomModelData(1))
        }
        return stack
    }

    override fun spawnChildFromBreeding(level: ServerLevel, partner: Animal) {
        try {
            val player: ServerPlayer? = this.loveCause ?: partner.loveCause

            player?.let {
                it.awardStat(Stats.ANIMALS_BRED)
                it.let { serverPlayer ->
                    CriteriaTriggers.BRED_ANIMALS.trigger(serverPlayer, this, partner, null)
                }
            }

            this.age = 6000
            partner.age = 6000
            this.resetLove()
            partner.resetLove()

            var currentSearchPos = this.blockPosition()
            var placed = false

            for (attempt in 0 until 8) {
                val validPos = (0..32).asSequence()
                    .map { currentSearchPos.below(it) }
                    .takeWhile { it.y >= level.minBuildHeight }
                    .firstOrNull { pos ->
                        val currentBlock = level.getBlockState(pos)
                        val blockUnder = level.getBlockState(pos.below())

                        currentBlock.fluidState.`is`(Fluids.WATER) &&
                                blockUnder.isFaceSturdy(level, pos.below(), Direction.UP)
                    }

                if (validPos != null) {
                    level.broadcastEntityEvent(this, 18.toByte())

                    val caviarState = AquaticBlocks.sturgeonCaviar.get().defaultBlockState()
                        .setValue(BlockStateProperties.WATERLOGGED, true)
                        .setValue(BlockStateProperties.DOWN, true)

                    level.setBlockAndUpdate(validPos, caviarState)

                    if (level.gameRules.getBoolean(GameRules.RULE_DOMOBLOOT)) {
                        val xpAmount = this.random.nextInt(7) + 1
                        level.addFreshEntity(ExperienceOrb(level, this.x, this.y, this.z, xpAmount))
                    }

                    placed = true
                    break
                } else {
                    val vec = DefaultRandomPos.getPos(this, 5, 1)

                    if (vec == null) {
                        ether.warn("Could not find a valid random breeding position for {}, skipping attempt", this)
                        continue
                    }

                    currentSearchPos = BlockPos.containing(vec.x, vec.y, vec.z)
                }
            }

            if (!placed) {
                ether.warn("Failed to place sturgeon caviar block for {}", this)
            }

        } catch (e: Exception) {
            ether.error("An error occurred during spawnChildFromBreeding: ", e)
        }
    }

    override fun setVariant(p0: SturgeonVariant) {
        this.setData(AquaticAttachments.sturgeon_variant,p0)
    }

    override fun getVariant(): SturgeonVariant = this.getData(AquaticAttachments.sturgeon_variant)

    override fun loadFromBucketTag(tag: CompoundTag) {
        super.loadFromBucketTag(tag)
        val variantIndex = tag.getInt("variant")
        if (variantIndex in SturgeonVariant.entries.indices) {
            variant = SturgeonVariant.entries[variantIndex]
        }
        setAge(tag.getInt("age"))
    }

    override fun saveToBucketTag(stack: ItemStack) {
        super.saveToBucketTag(stack)
        CustomData.update(DataComponents.BUCKET_ENTITY_DATA,stack) { nbt ->
            nbt["variant"] = variant.ordinal
            nbt["age"] = getAge()
        }
    }
}