//package stellar.aquatic.content.entity
//
//import net.minecraft.core.BlockPos
//import net.minecraft.core.Direction
//import net.minecraft.core.particles.ParticleTypes
//import net.minecraft.tags.BlockTags
//import net.minecraft.world.entity.*
//import net.minecraft.world.entity.item.FallingBlockEntity
//import net.minecraft.world.entity.projectile.ThrowableItemProjectile
//import net.minecraft.world.item.Item
//import net.minecraft.world.level.Level
//import net.minecraft.world.level.block.state.BlockState
//import net.minecraft.world.phys.*
//import net.neoforged.neoforge.common.Tags
//import stellar.aquatic.index.AquaticEntities.zloyPtichkaEntity
//import stellar.aquatic.index.AquaticItems.angrybird
//import stellar.aquatic.index.AquaticSounds.ptichka
//
//class PtichkaEntity : ThrowableItemProjectile {
//
//    constructor(type: EntityType<out PtichkaEntity>, level: Level) : super(type, level)
//
//    constructor(level: Level, shooter: LivingEntity) : super(zloyPtichkaEntity.get(), shooter, level)
//
//    override fun getDefaultItem(): Item {
//        return angrybird.get()
//    }
//
//    override fun getDefaultGravity(): Double {
//        return 0.03
//    }
//
//    override fun onAddedToLevel() {
//        super.onAddedToLevel()
//        this.playSound(ptichka.get())
//    }
//
//    override fun onHitEntity(result: EntityHitResult) {
//        super.onHitEntity(result)
//        if (!this.level().isClientSide) {
//            result.entity.hurt(this.damageSources().thrown(this, this.owner), 5f)
//            breakNearbyBlocks(result.entity.blockPosition())
//            this.discard()
//        }
//    }
//
//    override fun onHitBlock(result: BlockHitResult) {
//        super.onHitBlock(result)
//        if (!this.level().isClientSide) {
//            breakNearbyBlocks(result.blockPos)
//            this.discard()
//        }
//    }
//
//    private fun breakNearbyBlocks(center: BlockPos) {
//        val radius = 2
//        val radiusSq = (radius + 0.5) * (radius + 0.5)
//        val level = this.level()
//
//        // This set keeps track of all blocks that lost their neighbors and need structural checks
//        val physicsCheckQueue = mutableSetOf<BlockPos>()
//
//        for (x in -radius..radius) {
//            for (y in -radius..radius) {
//                for (z in -radius..radius) {
//                    if (x * x + y * y + z * z > radiusSq) continue
//
//                    val targetPos = center.offset(x, y, z)
//                    val state: BlockState = level.getBlockState(targetPos)
//
//                    if (state.isAir) continue
//
//                    // Categorize blocks into soft/wood and heavy/stone
//                    val isSoftWood = state.`is`(BlockTags.LOGS) ||
//                            state.`is`(BlockTags.PLANKS) ||
//                            state.`is`(BlockTags.LEAVES) ||
//                            state.`is`(BlockTags.WOODEN_FENCES) ||
//                            state.`is`(BlockTags.WOODEN_SLABS) ||
//                            state.`is`(BlockTags.WOODEN_STAIRS) ||
//                            state.`is`(BlockTags.WOODEN_DOORS) ||
//                            state.`is`(BlockTags.WOODEN_TRAPDOORS) ||
//                            state.`is`(BlockTags.MINEABLE_WITH_AXE) ||
//                            (state.getDestroySpeed(level, targetPos) in 0.0f..1.5f)
//
//                    val isHeavyStone = state.`is`(BlockTags.MINEABLE_WITH_PICKAXE) ||
//                            state.`is`(BlockTags.STONE_BRICKS) ||
//                            state.`is`(BlockTags.WALLS) ||
//                            state.soundType == net.minecraft.world.level.block.SoundType.STONE
//
//                    // If it's wood/soft, shatter it entirely
//                    if (isSoftWood) {
//                        level.destroyBlock(targetPos, true, this.owner)
//                        queueNeighbors(targetPos, physicsCheckQueue)
//                    }
//                    // If it's a stone structure, the blast knocks it loose into a falling block
//                    else if (isHeavyStone && state.getDestroySpeed(level, targetPos) >= 0f) {
//                        spawnFallingBlock(targetPos, state, level)
//                        queueNeighbors(targetPos, physicsCheckQueue)
//                    }
//                }
//            }
//        }
//
//        // After the initial blast, process the chain reaction for collapsing structures
//        processPhysicsQueue(physicsCheckQueue, level)
//    }
//
//    private fun queueNeighbors(pos: BlockPos, queue: MutableSet<BlockPos>) {
//        for (dir in Direction.values()) {
//            queue.add(pos.relative(dir))
//        }
//    }
//
//    private fun processPhysicsQueue(initialQueue: Set<BlockPos>, level: Level) {
//        val queue = ArrayDeque(initialQueue)
//        val processed = mutableSetOf<BlockPos>()
//        var iterations = 0
//        val maxIterations = 1500 // Safety cap to prevent server lag spikes on massive builds
//
//        while (queue.isNotEmpty() && iterations < maxIterations) {
//            val pos = queue.removeFirst()
//
//            // Skip if we already checked this block in this tick
//            if (!processed.add(pos)) continue
//
//            val state = level.getBlockState(pos)
//            if (state.isAir) continue
//
//            val isWood = state.`is`(BlockTags.LOGS) || state.`is`(BlockTags.PLANKS) || state.`is`(BlockTags.MINEABLE_WITH_AXE) || state.`is`(BlockTags.LEAVES)
//            val isStone = state.`is`(BlockTags.MINEABLE_WITH_PICKAXE)
//                    || state.`is`(BlockTags.STONE_BRICKS)
//                    || state.soundType == net.minecraft.world.level.block.SoundType.STONE
//                    || state.`is`(Tags.Blocks.STONES)
//                    || state.`is`(Tags.Blocks.COBBLESTONES)
//
//            if (isWood || isStone) {
//                // Check if the block has structural integrity
//                if (!isSupported(level, pos, state)) {
//                    iterations++
//
//                    if (isStone) {
//                        spawnFallingBlock(pos, state, level)
//                    } else {
//                        // Wood shatters instead of falling as a block
//                        level.destroyBlock(pos, true, this.owner)
//                    }
//
//                    // Because this block collapsed, its neighbors now need to be checked!
//                    for (dir in Direction.values()) {
//                        val neighborPos = pos.relative(dir)
//                        if (neighborPos !in processed) {
//                            queue.add(neighborPos)
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    private fun isSupported(level: Level, pos: BlockPos, state: BlockState, depth: Int = 0, visited: MutableSet<BlockPos> = mutableSetOf()): Boolean {
//        visited.add(pos)
//
//        val belowPos = pos.below()
//        val belowState = level.getBlockState(belowPos)
//
//        // Rule 1: Vertical Support.
//        // If the block below is solid (not air, and cannot be replaced like tall grass), it is fully supported.
//        if (!belowState.isAir && !belowState.canBeReplaced()) {
//            return true
//        }
//
//        // Rule 2: Overhang Limits.
//        // Simulates physics by only allowing a structural beam to span up to 2 blocks without support underneath.
//        if (depth >= 2) return false
//
//        // Rule 3: Horizontal Support (Cantilevers).
//        // Check horizontally to see if it is connected to a log/stone block that DOES have support.
//        for (dir in Direction.Plane.HORIZONTAL) {
//            val neighborPos = pos.relative(dir)
//            if (neighborPos in visited) continue
//
//            val neighborState = level.getBlockState(neighborPos)
//
//            // Only structural blocks can transfer support
//            val isStructural = neighborState.`is`(BlockTags.LOGS) ||
//                    neighborState.`is`(BlockTags.PLANKS) ||
//                    neighborState.`is`(BlockTags.MINEABLE_WITH_AXE) ||
//                    neighborState.`is`(BlockTags.MINEABLE_WITH_PICKAXE) ||
//                    neighborState.`is`(BlockTags.STONE_BRICKS)
//
//            if (isStructural) {
//                // Recursively check if the connected block is supported
//                if (isSupported(level, neighborPos, neighborState, depth + 1, visited)) {
//                    return true
//                }
//            }
//        }
//
//        return false // Has no blocks beneath it, and isn't horizontally attached to a supported block
//    }
//
//    private fun spawnFallingBlock(pos: BlockPos, state: BlockState, level: Level) {
//        // Prevent stacking duplicate falling blocks in the exact same spot
//        val bounds = AABB(pos)
//        if (level.getEntitiesOfClass(FallingBlockEntity::class.java, bounds).isNotEmpty()) {
//            return
//        }
//
//        // Create and setup the falling block entity
//        val fallingBlock = FallingBlockEntity(level,pos.x + 0.5, pos.y.toDouble(), pos.z + 0.5,state)
//        fallingBlock.time = 1 // Set to 1 so it starts falling immediately without despawning
//
//        // Replace the actual block with air (or water if waterlogged)
//        level.setBlock(pos, state.fluidState.createLegacyBlock(), 3)
//
//        // Spawn it into the world
//        level.addFreshEntity(fallingBlock)
//    }
//
//    override fun tick() {
//        super.tick()
//        if(this.level().isClientSide) {
//            this.level().addParticle(
//                ParticleTypes.END_ROD,
//                x,y,z,
//                0.0,0.0,0.0
//            )
//        }
//    }
//}