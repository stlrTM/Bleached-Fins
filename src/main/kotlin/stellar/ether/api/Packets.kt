package stellar.ether.api

import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.entity.Entity
import net.neoforged.neoforge.network.handling.IPayloadContext

fun <T : Entity> CustomPacketPayload.entity(id: Int,ctx: IPayloadContext,type: Class<T>): T? {
    val e = ctx.player().level().getEntity(id)
    if(e == null) return null
    if(e::class.java != type) return null
    return e as T
}