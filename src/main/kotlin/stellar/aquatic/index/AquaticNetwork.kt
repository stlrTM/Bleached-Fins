package stellar.aquatic.index

import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import stellar.aquatic.content.network.C2SSwordfishBoostStop
import stellar.aquatic.content.network.S2CSwordfishBoostStart
import stellar.aquatic.loc

@EventBusSubscriber(modid = "ether_aquatic")
object AquaticNetwork {
    val c2sSwordfishBoostStopType = CustomPacketPayload.Type<C2SSwordfishBoostStop>(loc("1"))
    val s2cSwordfishBoostStartType = CustomPacketPayload.Type<S2CSwordfishBoostStart>(loc("2"))

    @SubscribeEvent
    fun onNetwork(event: RegisterPayloadHandlersEvent) {
        val r = event.registrar("1")

        r.playToServer(
            c2sSwordfishBoostStopType,
            ByteBufCodecs.INT.map(
                ::C2SSwordfishBoostStop,
                C2SSwordfishBoostStop::entity
            ),
            C2SSwordfishBoostStop::handle
        )

        r.playToClient(
            s2cSwordfishBoostStartType,
            ByteBufCodecs.INT.map(
                ::S2CSwordfishBoostStart,
                S2CSwordfishBoostStart::entity
            ),
            S2CSwordfishBoostStart::handle
        )
    }
}