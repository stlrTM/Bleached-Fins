package stellar.ether.client.renderer.entity

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.model.EntityModel
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.LivingEntityRenderer
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.LivingEntity

abstract class MultimodelRenderer<E : LivingEntity>(
    val context: EntityRendererProvider.Context,
    defaultModel: EntityModel<E>,
    shadowRadius: Float
) : LivingEntityRenderer<E,EntityModel<E>>(
    context,
    defaultModel,
    shadowRadius
) {
    private var activeModel: EntityModel<E> = defaultModel

    override fun getModel(): EntityModel<E> = activeModel

    abstract fun selectModel(entity: E): EntityModel<E>
    abstract fun selectTexture(entity: E): ResourceLocation

    override fun getTextureLocation(p0: E): ResourceLocation = selectTexture(p0)

    override fun render(
        p_114485_: E,
        p_114486_: Float,
        p_114487_: Float,
        p_114488_: PoseStack,
        p_114489_: MultiBufferSource,
        p_114490_: Int
    ) {
        activeModel = selectModel(p_114485_)
        this.model = activeModel
        super.render(p_114485_, p_114486_, p_114487_, p_114488_, p_114489_, p_114490_)
    }
}