package stellar.aquatic.client.renderer.entity.piranha;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import stellar.aquatic.AquaticKt;
import stellar.aquatic.content.entity.piranha.arrow.PiranhaArrowEntity;

public class PiranhaArrowModel extends HierarchicalModel<PiranhaArrowEntity> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(AquaticKt.loc("piranha_arrow"), "main");
    private final ModelPart all;
    private final ModelPart back;
    private final ModelPart cross_1;
    private final ModelPart cross_2;

    public PiranhaArrowModel(ModelPart root) {
        this.all = root.getChild("all");
        this.back = all.getChild("back");
        this.cross_1 = all.getChild("cross_1");
        this.cross_2 = all.getChild("cross_2");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition all = partdefinition.addOrReplaceChild("all", CubeListBuilder.create(), PartPose.offset(0.0F, 0, 0.0F));
        PartDefinition back = all.addOrReplaceChild("back", CubeListBuilder.create().texOffs(0, 8).addBox(-2.0F, -1.0F, -2.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(0, 15).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(0.0F, 20.5F, 7.0F, -2.3562F, 1.5708F, 0.0F));
        PartDefinition cross_1 = all.addOrReplaceChild("cross_1", CubeListBuilder.create().texOffs(0, 0).addBox(-12.0F, -1.5F, 0.0F, 16.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 20.5F, -4.0F, -2.3562F, 1.5708F, 0.0F));
        PartDefinition cross_2 = all.addOrReplaceChild("cross_2", CubeListBuilder.create().texOffs(0, 4).addBox(-12.0F, -1.5F, 0.0F, 16.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 20.5F, -4.0F, -0.7854F, 1.5708F, 0.0F));
        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int i, int i1, int i2) {
        back.render(poseStack, vertexConsumer, i, i1, i2);
        cross_1.render(poseStack, vertexConsumer, i, i1, i2);
        cross_2.render(poseStack, vertexConsumer, i, i1, i2);
    }

    @Override
    public ModelPart root() {
        return all;
    }

    @Override
    public void setupAnim(PiranhaArrowEntity piranhaArrowEntity, float v, float v1, float v2, float v3, float v4) {

    }
}