//package stellar.aquatic.client.renderer.entity
//
//import com.mojang.blaze3d.vertex.PoseStack
//import net.minecraft.client.renderer.MultiBufferSource
//import net.minecraft.client.renderer.entity.*
//import net.minecraft.client.renderer.texture.OverlayTexture
//import net.minecraft.resources.ResourceLocation
//import net.minecraft.world.item.ItemDisplayContext
//import stellar.aquatic.content.entity.PtichkaEntity
//import stellar.ether.RandomSmoothRotations
//
//class PtichkaRender(
//    val ctx: EntityRendererProvider.Context
//) : EntityRenderer<PtichkaEntity>(ctx) {
//
//    override fun getTextureLocation(p0: PtichkaEntity): ResourceLocation = ResourceLocation.fromNamespaceAndPath("a", "a")
//
//    override fun render(
//        e: PtichkaEntity,
//        p_114486_: Float,
//        partial: Float,
//        pose: PoseStack,
//        mbs: MultiBufferSource,
//        overlay: Int
//    ) {
//        pose.pushPose()
//
//        val continuousTime = e.tickCount.toDouble() + partial.toDouble()
//
//        val rotationSeed = e.id.toLong()
//        val smoothRotation = RandomSmoothRotations.getRandomSmoothRotation(rotationSeed, continuousTime,.5f)
//        pose.mulPose(smoothRotation)
//
//        val packedLight = ctx.entityRenderDispatcher.getPackedLightCoords(e, partial)
//
//        ctx.itemRenderer.renderStatic(
//            e.item,
//            ItemDisplayContext.NONE,
//            packedLight,
//            OverlayTexture.NO_OVERLAY,
//            pose,
//            mbs,
//            e.level(),
//            e.id
//        )
//
//        pose.popPose()
//        super.render(e, p_114486_, partial, pose, mbs, overlay)
//    }
//}