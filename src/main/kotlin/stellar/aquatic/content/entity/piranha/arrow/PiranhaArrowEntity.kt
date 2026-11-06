package stellar.aquatic.content.entity.piranha.arrow

import net.minecraft.core.*
import net.minecraft.sounds.*
import net.minecraft.world.entity.*
import net.minecraft.world.entity.projectile.AbstractArrow
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.*
import net.minecraft.world.phys.BlockHitResult
import stellar.aquatic.content.block.entity.PiranhaTrapBlockEntity
import stellar.aquatic.index.AquaticBlocks
import stellar.aquatic.index.AquaticEntities
import stellar.aquatic.index.AquaticItems


class PiranhaArrowEntity : AbstractArrow {
    constructor(entityType: EntityType<out PiranhaArrowEntity?>, level: Level) : super(entityType, level)

    constructor(level: Level, owner: LivingEntity, pickupItemStack: ItemStack, firedFromWeapon: ItemStack?) : super(
        AquaticEntities.piranhaArrow.get(),
        owner,
        level,
        pickupItemStack,
        firedFromWeapon
    )

    constructor(
        level: Level,
        x: Double,
        y: Double,
        z: Double,
        pickupItemStack: ItemStack,
        firedFromWeapon: ItemStack?
    ) : super(AquaticEntities.piranhaArrow.get(), x, y, z, level, pickupItemStack, firedFromWeapon)

    override fun onHitBlock(result: BlockHitResult) {
        if (! level().isClientSide()) {
            if (result.getDirection() == Direction.UP) {
                val hitPos = result.getBlockPos()
                val hitState = level().getBlockState(hitPos)
                var placePos: BlockPos? = null
                if (hitState.canBeReplaced()) {
                    placePos = hitPos
                    if (! hitState.isAir()) {
                        level().destroyBlock(hitPos, true)
                    }
                } else {
                    val abovePos = hitPos.above()
                    val aboveState = level().getBlockState(abovePos)
                    if (aboveState.isAir() || aboveState.canBeReplaced()) {
                        placePos = abovePos
                        if (! aboveState.isAir()) {
                            level().destroyBlock(abovePos, true)
                        }
                    }
                }
                if (placePos != null) {
                    level().setBlock(
                        placePos,
                        AquaticBlocks.piranhaTrap().defaultBlockState()
                            .setValue(HorizontalDirectionalBlock.FACING, direction.clockWise),
                        3
                    )
                    val be = level().getBlockEntity(placePos)
                    if ((be is PiranhaTrapBlockEntity) && (this.owner != null)) {
                        be.setOwner(this.owner)
                    }
                    level().playSound(
                        null,
                        placePos,
                        AquaticBlocks.piranhaTrap().defaultBlockState().getSoundType().getBreakSound(),
                        SoundSource.BLOCKS
                    )
                    level().playSound(null, placePos, SoundEvents.FISHING_BOBBER_RETRIEVE, SoundSource.BLOCKS)
                    level().levelEvent(2001, placePos, Block.getId(AquaticBlocks.piranhaTrap().defaultBlockState()))
                } else {
                    this.spawnAtLocation(defaultPickupItem, .1f)
                }
            } else {
                this.spawnAtLocation(defaultPickupItem, .1f)
            }
            this.discard()
        }
        super.onHitBlock(result)
    }

    override fun getDefaultPickupItem(): ItemStack {
        return ItemStack(AquaticItems.piranhaArrow.get())
    }

    override fun tick() {
        super.tick()
    }


    override fun isPickable(): Boolean {
        return false
    }
}