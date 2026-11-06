package stellar.aquatic.index

import stellar.aquatic.Aquatic.registrate
import stellar.aquatic.content.block.entity.PiranhaTrapBlockEntity
import stellar.ether.api.Loadable
import stellar.ether.api.registrate.builders.BlockEntityBuilders.blockEntity

object AquaticBlockEntities : Loadable {
    val piranhaTrapBlockEntity = registrate.blockEntity(
        "piranha_trap",
        ::PiranhaTrapBlockEntity
    ) {
        validBlock(AquaticBlocks.piranhaTrap)
    }
}