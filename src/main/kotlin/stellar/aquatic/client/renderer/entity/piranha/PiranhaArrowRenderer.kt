package stellar.aquatic.client.renderer.entity.piranha

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.renderer.*
import net.minecraft.client.renderer.entity.*
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import stellar.aquatic.content.entity.piranha.arrow.PiranhaArrowEntity
import stellar.aquatic.loc

class PiranhaArrowRenderer(context: EntityRendererProvider.Context) : EntityRenderer<PiranhaArrowEntity>(context) {
    private val model: PiranhaArrowModel

    init {
        this.model = PiranhaArrowModel(context.bakeLayer(PiranhaArrowModel.LAYER_LOCATION))
    }

    override fun render(
        p_113839_: PiranhaArrowEntity,
        p_113840_: Float,
        p_113841_: Float,
        p_113842_: PoseStack,
        p_113843_: MultiBufferSource,
        p_113844_: Int
    ) {
        p_113842_.pushPose()
        p_113842_.mulPose(Axis.YP.rotationDegrees(Mth.lerp(p_113841_, p_113839_.yRotO, p_113839_.getYRot()) - 90.0f))
        p_113842_.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(p_113841_, p_113839_.xRotO, p_113839_.getXRot())))
        val f9 = p_113839_.shakeTime - p_113841_
        if (f9 > 0.0f) {
            val f10 = - Mth.sin(f9 * 3.0f) * f9
            p_113842_.mulPose(Axis.ZP.rotationDegrees(f10))
        }
        p_113842_.mulPose(Axis.XN.rotationDegrees(- 135f))
        p_113842_.mulPose(Axis.YN.rotationDegrees(90f))
        p_113842_.scale(0.7f, 0.7f, 0.7f)
        p_113842_.translate(0f, - 1.3f, - .5f)
        this.model.renderToBuffer(
            p_113842_,
            p_113843_.getBuffer(RenderType.entityCutout(this.getTextureLocation(p_113839_))),
            p_113844_,
            OverlayTexture.NO_OVERLAY
        )
        p_113842_.popPose()
        super.render(p_113839_, p_113840_, p_113841_, p_113842_, p_113843_, p_113844_)
    }

    override fun getTextureLocation(entity: PiranhaArrowEntity): ResourceLocation {
        return loc("textures/entity/piranha_arrow.png")
    }
}