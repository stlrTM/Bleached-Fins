package stellar.ether.api

import net.minecraft.nbt.CompoundTag

object NbtExtensions {
    operator fun CompoundTag.set(id: String,data: Int) = this.putInt(id,data)

}