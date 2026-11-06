package stellar.aquatic.content.block.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import stellar.aquatic.index.AquaticBlockEntities
import java.util.UUID

class PiranhaTrapBlockEntity(
    pos: BlockPos,
    state: BlockState
) : BlockEntity(AquaticBlockEntities.piranhaTrapBlockEntity.get(), pos, state) {

    var bite: Int = 0
        set(value) {
            field = value
            setChanged()
        }

    private var ownerUUID: UUID? = null
    private var cachedOwner: Entity? = null

    fun setOwner(owner: Entity?) {
        if (owner != null) {
            this.ownerUUID = owner.uuid
            this.cachedOwner = owner
        }
    }

    fun getOwner(level: Level): Entity? {
        val cached = cachedOwner
        if (cached != null && !cached.isRemoved) {
            return cached
        }
        
        val uuid = ownerUUID
        if (uuid != null && level is ServerLevel) {
            cachedOwner = level.getEntity(uuid)
            return cachedOwner
        }
        return null
    }

    fun addBite(amount: Int): Int {
        bite += amount
        return bite
    }

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)
        tag.putInt("Bite", bite)
        ownerUUID?.let { tag.putUUID("Owner", it) }
    }

    override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(tag, registries)
        if (tag.contains("Bite")) {
            this.bite = tag.getInt("Bite")
        }
        if (tag.hasUUID("Owner")) {
            this.ownerUUID = tag.getUUID("Owner")
            this.cachedOwner = null
        }
    }
}