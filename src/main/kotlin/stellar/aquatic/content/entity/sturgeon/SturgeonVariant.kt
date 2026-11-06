package stellar.aquatic.content.entity.sturgeon

import com.mojang.serialization.Codec
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.util.StringRepresentable

enum class SturgeonVariant(
    val original: String,
    val texture: String,
    val babyTexture: String
) : StringRepresentable {
    GREEN("green"),
    SEA("sea"),
    SWAMP("swamp");

    companion object {
        // compat with old system
        val CODEC: Codec<SturgeonVariant> = Codec.withAlternative(
            StringRepresentable.fromValues(SturgeonVariant::values),
            Codec.INT.xmap(
                { SturgeonVariant.entries[it]},
                SturgeonVariant::ordinal
            )
        )
        val STREAM_CODEC = ByteBufCodecs.idMapper({ entries[it] }, SturgeonVariant::ordinal)
    }

    override fun getSerializedName(): String = original

    constructor(texture: String) : this(texture,"textures/entity/fish/sturgeon/${texture}.png","textures/entity/fish/sturgeon/${texture}_baby.png")
}