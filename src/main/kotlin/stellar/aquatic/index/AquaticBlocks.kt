package stellar.aquatic.index

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.tags.BlockTags
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.material.MapColor
import net.minecraft.world.level.material.PushReaction
import stellar.aquatic.Aquatic.registrate
import stellar.aquatic.content.block.PiranhaTrapBlock
import stellar.aquatic.content.block.SturgeonCaviarBlock
import stellar.ether.api.Loadable
import stellar.ether.api.registrate.builders.BlockBuilders.block
import stellar.ether.api.registrate.datagen.Providers


object AquaticBlocks : Loadable {

    val sturgeonCaviar = registrate.block(
        "sturgeon_caviar",
        ::SturgeonCaviarBlock
    ) {
        addData(Providers.BLOCK_TAGS) { ctx ->
            add(BlockTags.INSIDE_STEP_SOUND_BLOCKS).add(BuiltInRegistries.BLOCK.getResourceKey(ctx()).get())
        }
        properties {
            mapColor(MapColor.GLOW_LICHEN)
            replaceable()
            noCollission()
            strength(0.2F)
            sound(SoundType.GLOW_LICHEN)
            pushReaction(PushReaction.DESTROY)
            randomTicks()
            ignitedByLava()
            sound(SoundType.SLIME_BLOCK)
        }
    }

    val piranhaTrap = registrate.block(
        "piranha_trap",
        ::PiranhaTrapBlock
    ) {
        addData(Providers.LANG) { ctx ->
            add(ctx(),"Piranha Trap")
        }
        properties {
            mapColor(MapColor.GLOW_LICHEN)
            noCollission()
            strength(10.2F)
            sound(SoundType.MUD)
            offsetType(BlockBehaviour.OffsetType.XZ)
            pushReaction(PushReaction.DESTROY)
        }
    }
}