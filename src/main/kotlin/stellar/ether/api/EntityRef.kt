package stellar.ether.api

import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level
import net.neoforged.neoforge.common.util.INBTSerializable
import java.util.UUID

class EntityRef<T : Entity>(
    private val entityClass: Class<T>? = null
) : INBTSerializable<CompoundTag> {

    private var cache: T? = null
    var id: UUID? = null
        private set

    @Suppress("UNCHECKED_CAST")
    fun get(level: Level): T? {
        val current = cache
        if (current != null && !current.isRemoved && current.level() == level) {
            return current
        }

        val targetId = id ?: return null

        val fetched = if (level is ServerLevel) {
            level.getEntity(targetId)
        } else {
            level.entities.get(targetId)
        }

        if (fetched != null && !fetched.isRemoved) {
            if (entityClass == null || entityClass.isInstance(fetched)) {
                val typed = fetched as T
                this.cache = typed
                return typed
            }
        }

        this.cache = null
        return null
    }

    fun isPresent(): Boolean = cache != null

    fun set(entity: T?) {
        if (entity != null) {
            this.id = entity.uuid
            this.cache = entity
        } else {
            clear()
        }
    }

    fun clear() {
        this.id = null
        this.cache = null
    }

    fun invalidateCache() {
        this.cache = null
    }

    override fun serializeNBT(provider: HolderLookup.Provider): CompoundTag {
        val tag = CompoundTag()
        id?.let { tag.putUUID("ref", it) }
        return tag
    }

    override fun deserializeNBT(provider: HolderLookup.Provider, tag: CompoundTag) {
        id = if (tag.hasUUID("ref")) tag.getUUID("ref") else null
        invalidateCache()
    }
}