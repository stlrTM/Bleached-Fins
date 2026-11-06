package stellar.aquatic.index

import com.mojang.serialization.Codec
import stellar.aquatic.Aquatic.registrate
import stellar.aquatic.content.entity.sturgeon.SturgeonVariant
import stellar.ether.api.Loadable
import stellar.ether.api.registrate.builders.AttachmentBuilders.attachment

object AquaticAttachments : Loadable {
    val sturgeon_variant = registrate.attachment(
        "sturgeon_variant",
        { SturgeonVariant.GREEN }
    ) {
        properties {
            serialize(SturgeonVariant.CODEC)
            sync(SturgeonVariant.STREAM_CODEC)
        }
    }

    val player_riding_swordfish = registrate.attachment(
        "riding_swordfish",
        { false }
    )
}