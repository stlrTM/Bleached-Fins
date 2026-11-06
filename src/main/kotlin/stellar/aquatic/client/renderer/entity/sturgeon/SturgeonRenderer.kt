package stellar.aquatic.client.renderer.entity.sturgeon

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.model.EntityModel
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import org.joml.Quaternionf
import stellar.aquatic.client.renderer.entity.sturgeon.model.BabySturgeonEntityModel
import stellar.aquatic.client.renderer.entity.sturgeon.model.SturgeonEntityModel
import stellar.aquatic.content.entity.sturgeon.SturgeonEntity
import stellar.aquatic.index.positionVelocity
import stellar.aquatic.loc
import stellar.ether.client.renderer.entity.MultimodelRenderer

class SturgeonRenderer(
    ctx: EntityRendererProvider.Context,
    val default: SturgeonEntityModel,
    val baby: BabySturgeonEntityModel
) : MultimodelRenderer<SturgeonEntity>(
    ctx,
    default,
    .4f
) {
    override fun selectModel(entity: SturgeonEntity): EntityModel<SturgeonEntity> {
        return if (entity.isBaby) {
            baby
        } else {
            default
        }
    }

    override fun selectTexture(entity: SturgeonEntity): ResourceLocation {
        val variant = entity.variant
        return if(entity.isBaby) {
            loc(variant.babyTexture)
        } else {
            loc(variant.texture)
        }
    }

    override fun render(
        e: SturgeonEntity,
        p_114486_: Float,
        partial: Float,
        pose: PoseStack,
        mbs: MultiBufferSource,
        light: Int
    ) {
        if (e.isBaby) {
            pose.scale(.5f, .5f, .5f)
        }

        if (!e.isInWater) {
            pose.translate(0.2f, 0.1f, 0.0f)
        }
        super.render(e, p_114486_, partial,pose,mbs,light)
    }

    override fun setupRotations(
        e: SturgeonEntity,
        pose: PoseStack,
        age: Float,
        yaw: Float,
        partial: Float,
        scale: Float
    ) {
        super.setupRotations(e,pose,age,yaw,partial,scale)

        val quat = Quaternionf()

        val targetPitch = e.positionVelocity().y * 40.0
        val smoothedPitch = e.clientPitch.update(
            delta = partial,
            target = targetPitch.toFloat()
        )

        quat.rotateX(
            Mth.clamp(smoothedPitch * 4.0f, -65.0f, 65.0f) * Mth.DEG_TO_RAD
        )

        pose.mulPose(quat)
    }


    override fun shouldShowName(p_115506_: SturgeonEntity): Boolean {
        return super.shouldShowName(p_115506_) && (p_115506_.shouldShowName() || p_115506_.hasCustomName() && p_115506_ === this.entityRenderDispatcher.crosshairPickEntity)
    }
}