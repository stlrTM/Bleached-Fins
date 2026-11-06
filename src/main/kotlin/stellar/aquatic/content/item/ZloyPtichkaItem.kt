//package stellar.aquatic.content.item
//
//import net.minecraft.world.InteractionHand
//import net.minecraft.world.InteractionResultHolder
//import net.minecraft.world.entity.LivingEntity
//import net.minecraft.world.entity.player.Player
//import net.minecraft.world.item.Item
//import net.minecraft.world.item.ItemStack
//import net.minecraft.world.item.ItemUtils
//import net.minecraft.world.item.UseAnim
//import net.minecraft.world.level.Level
//import stellar.aquatic.content.entity.PtichkaEntity
//import stellar.aquatic.index.AquaticSounds.slingshot
//
//class ZloyPtichkaItem(
//    settings: Properties,
//) : Item(settings) {
//
//    private val maxDuration = 72000
//    private val fullChargeTicks = 20f
//
//    override fun getUseDuration(stack: ItemStack, entity: LivingEntity): Int = maxDuration
//
//    override fun releaseUsing(stack: ItemStack, level: Level, entity: LivingEntity, count: Int) {
//        val ticksUsed = maxDuration - count
//        val percentage = (ticksUsed.toFloat() / fullChargeTicks).coerceAtMost(1.0f)
//
//        if (percentage < 0.1f) return
//
//        if (!level.isClientSide) {
//            val ptichka = PtichkaEntity(level, entity)
//            ptichka.item = stack.copy()
//
//            ptichka.shootFromRotation(
//                entity,
//                entity.xRot,
//                entity.yRot,
//                0.0f,
//                2.0f * percentage,
//                1.0f
//            )
//
//            stack.consume(1, entity)
//            level.addFreshEntity(ptichka)
//        }
//    }
//
//    override fun getUseAnimation(stack: ItemStack): UseAnim {
//        return UseAnim.BOW
//    }
//
//    override fun onUseTick(level: Level, livingEntity: LivingEntity, stack: ItemStack, remainingUseDuration: Int) {
//        if(remainingUseDuration % 12 == 0 && remainingUseDuration > 0) {
//            livingEntity.playSound(
//                slingshot.get(),
//            )
//        }
//    }
//
//    override fun use(
//        level: Level,
//        player: Player,
//        hand: InteractionHand
//    ): InteractionResultHolder<ItemStack> {
//        return ItemUtils.startUsingInstantly(level, player, hand)
//    }
//}