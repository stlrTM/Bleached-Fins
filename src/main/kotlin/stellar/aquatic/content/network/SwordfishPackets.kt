package stellar.aquatic.content.network

import net.minecraft.client.Minecraft
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.entity.Pose
import net.neoforged.neoforge.network.handling.IPayloadContext
import stellar.aquatic.client.handler.SwordfishBoostHandler
import stellar.aquatic.content.entity.swordfish.SwordfishEntity
import stellar.aquatic.index.AquaticAttachments
import stellar.aquatic.index.AquaticNetwork
import stellar.ether.api.entity

data class C2SSwordfishBoostStop(
    val entity: Int
) : CustomPacketPayload {
    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload?> = AquaticNetwork.c2sSwordfishBoostStopType

    fun handle(ctx: IPayloadContext) {
        ctx.enqueueWork {
            val e = entity(entity,ctx, SwordfishEntity::class.java)
            if(e == null || !e.rider.isPresent()) return@enqueueWork
            e.lastRider.set(e.rider.get(e.level()))
            e.rider.get(e.level())!!.forcedPose = null
            e.rider.clear()
            e.runTime = 512
            ctx.player().setData(AquaticAttachments.player_riding_swordfish,false)
        }
    }
}

data class S2CSwordfishBoostStart(
    val entity: Int
) : CustomPacketPayload {
    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload?> = AquaticNetwork.s2cSwordfishBoostStartType

    fun handle(ctx: IPayloadContext) {

        ctx.enqueueWork {
            val e = entity(entity, ctx, SwordfishEntity::class.java) ?: return@enqueueWork
            SwordfishBoostHandler.fish = e
            SwordfishBoostHandler.shouldJump = true
            SwordfishBoostHandler.rideTime = 0
            val player = Minecraft.getInstance().player ?: return@enqueueWork
            player.forcedPose = Pose.SWIMMING
        }
    }
}