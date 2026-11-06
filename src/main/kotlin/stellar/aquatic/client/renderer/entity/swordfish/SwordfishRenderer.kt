package stellar.aquatic.client.renderer.entity.swordfish

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.entity.*
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import org.joml.Quaternionf
import stellar.aquatic.client.renderer.entity.swordfish.model.SwordfishModel
import stellar.aquatic.content.entity.swordfish.SwordfishEntity
import stellar.aquatic.index.positionVelocity
import stellar.aquatic.index.positionVelocity
import stellar.aquatic.loc

class SwordfishRenderer(
    ctx: EntityRendererProvider.Context
) : MobRenderer<SwordfishEntity,SwordfishModel>(
    ctx,
    SwordfishModel(ctx.bakeLayer(SwordfishModel.LAYER_LOCATION)),
    .4f
) {
    override fun getTextureLocation(p0: SwordfishEntity): ResourceLocation = loc("textures/entity/fish/swordfish.png")

    override fun setupRotations(
        e: SwordfishEntity,
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
        if (e.isBaby) {
            pose.scale(0.5f, 0.5f, 0.5f)
        }
        if (!e.isInWater) {
            pose.translate(0.2f, 0.1f, 0.0f)
        }
    }
}