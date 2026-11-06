package stellar.aquatic.content.block

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.core.registries.Registries
import net.minecraft.server.level.ServerLevel
import net.minecraft.tags.ItemTags
import net.minecraft.util.ParticleUtils
import net.minecraft.util.RandomSource
import net.minecraft.util.valueproviders.ConstantInt
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.level.storage.loot.LootParams
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import net.minecraft.world.phys.Vec3
import stellar.aquatic.index.AquaticEntities
import stellar.aquatic.index.AquaticItems

class SturgeonCaviarBlock(
    settings: Properties
) : MultifaceBlock(settings), SimpleWaterloggedBlock {
    companion object {
        val codec = simpleCodec(::SturgeonCaviarBlock)
    }
    private val spreader = MultifaceSpreader(this)

    override fun codec(): MapCodec<out MultifaceBlock?> = codec

    override fun getSpreader(): MultifaceSpreader = spreader

    init {
        this.registerDefaultState(
            this.defaultBlockState()
                .setValue(BlockStateProperties.WATERLOGGED, false)
        )

    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
        super.createBlockStateDefinition(builder)
        builder.add(BlockStateProperties.WATERLOGGED)
    }

    override fun updateShape(
        p_153302_: BlockState,
        p_153303_: Direction,
        p_153304_: BlockState,
        p_153305_: LevelAccessor,
        p_153306_: BlockPos,
        p_153307_: BlockPos
    ): BlockState {
        if (p_153302_.getValue(BlockStateProperties.WATERLOGGED)) {
            p_153305_.scheduleTick(p_153306_, Fluids.WATER, Fluids.WATER.getTickDelay(p_153305_))
        }

        return super.updateShape(p_153302_, p_153303_, p_153304_, p_153305_, p_153306_, p_153307_)
    }

    override fun isRandomlyTicking(state: BlockState): Boolean {
        return state.getValue(BlockStateProperties.WATERLOGGED)
    }

    override fun randomTick(state: BlockState, level: ServerLevel, pos: BlockPos, random: RandomSource) {
        for (direction in availableFaces(state)) {
            val prop = getFaceProperty(direction)

            if (random.nextInt(5) == 0) {
                val sturgeon = AquaticEntities.sturgeon.create(level)
                sturgeon.isBaby = true
                sturgeon.setPos(pos.center)
                sturgeon.setFromBucket(true)
                level.addFreshEntity(sturgeon)

                val s = state.setValue(prop, false)

                level.setBlockAndUpdate(pos, s)

                var any = false
                for (dir2 in availableFaces(s)) {
                    any = any || s.getValue(getFaceProperty(dir2))
                }

                if (!any) {
                    level.setBlockAndUpdate(
                        pos,
                        if (state.getValue(BlockStateProperties.WATERLOGGED)) Blocks.WATER.defaultBlockState() else Blocks.AIR.defaultBlockState()
                    )
                }
            }

            ParticleUtils.spawnParticlesOnBlockFace(
                level,
                pos,
                ParticleTypes.HAPPY_VILLAGER,
                ConstantInt.of(5),
                direction,
                { Vec3.ZERO },
                3.0
            )
        }
    }

    override fun getDrops(state: BlockState, params: LootParams.Builder): MutableList<ItemStack?> {
        val amount = availableFaces(state).size

        val tool = params.getOptionalParameter(LootContextParams.TOOL)
        if (tool != null) {
            if ((tool.`is`(Items.SHEARS)
                        || tool.`is`(ItemTags.HOES)
                        || tool.`is`(ItemTags.AXES)
                        || tool.`is`(ItemTags.PICKAXES)
                        || tool.`is`(ItemTags.SHOVELS)
                        ) && tool.getEnchantmentLevel(
                    params.level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT)
                        .getOrThrow(Enchantments.SILK_TOUCH)
                ) >= 1
            ) {
                val l = ArrayList<ItemStack?>()

                repeat(amount) {
                    l.add(AquaticItems.sturgeonCaviarItem.create(1))
                }

                return l
            }
        }
        return mutableListOf()
    }

    override fun canBeReplaced(state: BlockState, useContext: BlockPlaceContext): Boolean {
        return !useContext.itemInHand.`is`(AquaticItems.sturgeonCaviarItem.get()) || super.canBeReplaced(
            state,
            useContext
        )
    }

    override fun getFluidState(state: BlockState): FluidState {
        return if (state.getValue(BlockStateProperties.WATERLOGGED)) Fluids.WATER.getSource(false) else super.getFluidState(state)
    }

    override fun propagatesSkylightDown(state: BlockState, p_181226_: BlockGetter, p_181227_: BlockPos): Boolean {
        return state.fluidState.isEmpty
    }
}