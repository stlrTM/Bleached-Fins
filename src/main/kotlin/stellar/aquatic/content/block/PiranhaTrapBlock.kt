package stellar.aquatic.content.block

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.util.RandomSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.FallingBlock
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.SimpleWaterloggedBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.DirectionProperty
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.level.pathfinder.PathComputationType
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import stellar.aquatic.content.block.entity.PiranhaTrapBlockEntity
import stellar.aquatic.content.entity.piranha.arrow.PiranhaArrowEntity
import stellar.aquatic.index.AquaticBlockEntities
import stellar.aquatic.index.AquaticEntities
import stellar.aquatic.index.AquaticItems

class PiranhaTrapBlock(properties: Properties) : FallingBlock(properties), SimpleWaterloggedBlock, EntityBlock {

    init {
        registerDefaultState(
            stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(STATE, false)
                .setValue(WATERLOGGED, false)
        )
    }

    override fun codec(): MapCodec<out FallingBlock> = CODEC

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return AquaticBlockEntities.piranhaTrapBlockEntity.create(pos,state)
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState? {
        val fluidState = context.level.getFluidState(context.clickedPos)
        val isWater = fluidState.type == Fluids.WATER
        return defaultBlockState()
            .setValue(FACING, context.horizontalDirection.clockWise)
            .setValue(WATERLOGGED, isWater)
    }

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        val vec3 = state.getOffset(level, pos)
        return SHAPE.move(vec3.x, vec3.y, vec3.z)
    }

    override fun isPathfindable(state: BlockState, pathComputationType: PathComputationType): Boolean {
        return false
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(FACING, STATE, WATERLOGGED)
    }

    override fun entityInside(state: BlockState, level: Level, pos: BlockPos, entity: Entity) {
        entity.makeStuckInBlock(state, Vec3(0.8, 0.75, 0.8))
        if (!level.isClientSide && (entity is LivingEntity || entity is ItemEntity) && !entity.isSpectator && !level.blockTicks.hasScheduledTick(pos, this)) {
            level.scheduleTick(pos, this, 0)
        }
        super.entityInside(state, level, pos, entity)
    }

    override fun tick(state: BlockState, level: ServerLevel, pos: BlockPos, random: RandomSource) {
        if (level.isClientSide) {
            super.tick(state, level, pos, random)
            return
        }

        val be = level.getBlockEntity(pos)
        if (be !is PiranhaTrapBlockEntity) {
            super.tick(state, level, pos, random)
            return
        }

        val entities = level.getEntitiesOfClass(
            Entity::class.java,
            Shapes.block().bounds().move(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble())
        )
        val active = entities.isNotEmpty()
        val current = state.getValue(STATE)

        if (active != current) {
            level.setBlock(pos, state.setValue(STATE, active), 3)
        }

        val owner = be.getOwner(level)
        for (target in entities) {
            when (target) {
                is ItemEntity -> {
                    if (target.hurt(level.damageSources().generic(), 4f)) {
                        if (!target.isAlive) {
                            if (be.addBite(1) >= 6) {
                                level.destroyBlock(pos, false)
                            }
                            val pitch = if (be.bite >= 6) 0.5f else 1.3f
                            level.playSound(null, pos, SoundEvents.FOX_BITE, SoundSource.BLOCKS, 1f, pitch)
                            val breakPitch = (level.random.nextFloat() - level.random.nextFloat()) * 0.2f + 1.0f
                            level.playSound(null, pos, SoundEvents.ITEM_BREAK, SoundSource.BLOCKS, 0.8f, breakPitch)
                        }
                    }
                }
                is LivingEntity -> {
                    val damageSource = if (owner == null) {
                        level.damageSources().cactus()
                    } else {
                        level.damageSources().mobProjectile(
                            PiranhaArrowEntity(AquaticEntities.piranhaArrow.get(), level),
                            owner as? LivingEntity
                        )
                    }

                    val motion = target.deltaMovement
                    if (target.hurt(damageSource, 4f)) {
                        if (be.addBite(1) >= 6) {
                            level.destroyBlock(pos, false)
                        }
                        val pitch = if (be.bite >= 6) 0.5f else 1.0f
                        level.playSound(null, pos, SoundEvents.FOX_BITE, SoundSource.BLOCKS, 1f, pitch)
                        target.deltaMovement = motion
                        target.hasImpulse = false
                    }
                }
            }
        }

        if (active) {
            level.scheduleTick(pos, this, 0)
        }
        super.tick(state, level, pos, random)
    }

    override fun animateTick(state: BlockState, level: Level, pos: BlockPos, random: RandomSource) {}

    override fun getCloneItemStack(
        state: BlockState,
        target: HitResult,
        level: LevelReader,
        pos: BlockPos,
        player: Player
    ): ItemStack {
        return ItemStack(AquaticItems.piranhaArrow.get())
    }

    override fun getFluidState(state: BlockState): FluidState {
        return if (state.getValue(WATERLOGGED)) Fluids.WATER.getSource(false) else super.getFluidState(state)
    }

    override fun updateShape(
        state: BlockState,
        facing: Direction,
        facingState: BlockState,
        level: LevelAccessor,
        currentPos: BlockPos,
        facingPos: BlockPos
    ): BlockState {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level))
        }
        return super.updateShape(state, facing, facingState, level, currentPos, facingPos)
    }

    companion object {
        @JvmField
        val CODEC: MapCodec<PiranhaTrapBlock> = simpleCodec(::PiranhaTrapBlock)
        @JvmField
        val FACING: DirectionProperty = HorizontalDirectionalBlock.FACING
        @JvmField
        val WATERLOGGED: BooleanProperty = BlockStateProperties.WATERLOGGED
        @JvmField
        val STATE: BooleanProperty = BooleanProperty.create("state")
        @JvmField
        val SHAPE: VoxelShape = box(4.0, 0.0, 4.0, 12.0, 8.0, 12.0)
    }
}