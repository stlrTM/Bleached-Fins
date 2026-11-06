package stellar.aquatic.client.renderer.entity.piranha

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.entity.*
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import org.joml.Quaternionf
import stellar.aquatic.content.entity.piranha.PiranhaEntity
import stellar.aquatic.index.positionVelocity
import stellar.aquatic.loc

class PiranhaRenderer(
    ctx: EntityRendererProvider.Context
) : MobRenderer<PiranhaEntity, PiranhaEntityModel>(
    ctx,
    PiranhaEntityModel(ctx.bakeLayer(PiranhaEntityModel.LAYER_LOCATION)),
    .3f
) {
    override fun getTextureLocation(p0: PiranhaEntity): ResourceLocation = loc("textures/entity/fish/piranha.png")

    override fun setupRotations(
        e: PiranhaEntity,
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
            Mth.clamp(smoothedPitch * 16.0f, -65.0f, 65.0f) * Mth.DEG_TO_RAD
        )

        pose.mulPose(quat)
        if (!e.isInWater) {
            pose.translate(0.2f, 0.1f, 0.0f)
        }
    }
}