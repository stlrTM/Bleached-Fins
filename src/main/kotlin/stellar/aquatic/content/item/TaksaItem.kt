package stellar.aquatic.content.item
//
//import net.minecraft.sounds.SoundSource
//import net.minecraft.world.InteractionHand
//import net.minecraft.world.InteractionResultHolder
//import net.minecraft.world.entity.player.Player
//import net.minecraft.world.item.Item
//import net.minecraft.world.item.ItemStack
//import net.minecraft.world.level.Level
//import stellar.aquatic.index.AquaticSounds
//
//class TaksaItem(
//    settings: Properties,
//) : Item(settings) {
//    override fun use(
//        level: Level,
//        player: Player,
//        hand: InteractionHand
//    ): InteractionResultHolder<ItemStack> {
//        val itemstack = player.getItemInHand(hand)
//
//        if (!level.isClientSide) {
//            level.playSound(
//                null,
//                player.x,
//                player.y,
//                player.z,
//                AquaticSounds.taksa(),
//                SoundSource.PLAYERS,
//                1.0f,
//                1.0f
//            )
//        }
//
//        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide)
//    }
//}