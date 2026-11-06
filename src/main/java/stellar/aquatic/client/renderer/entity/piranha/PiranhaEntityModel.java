package stellar.aquatic.client.renderer.entity.piranha;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.joml.Vector3f;
import stellar.aquatic.AquaticKt;
import stellar.aquatic.content.entity.piranha.PiranhaEntity;

public class PiranhaEntityModel extends HierarchicalModel<PiranhaEntity> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(AquaticKt.loc("piranha"), "main");
	private final ModelPart all;
	private final ModelPart piranha;
	private final ModelPart jaw;

	public PiranhaEntityModel(ModelPart root) {
		this.all = root.getChild("all");
		this.piranha = this.all.getChild("piranha");
		this.jaw = this.piranha.getChild("jaw");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		PartDefinition all = partdefinition.addOrReplaceChild("all", CubeListBuilder.create(), PartPose.offset(0.0F, 0, 0.0F));
		PartDefinition piranha = all.addOrReplaceChild("piranha", CubeListBuilder.create().texOffs(0, 0).addBox(-1.6429F, -2.5F, -4.5F, 3.0F, 5.0F, 7.0F, new CubeDeformation(0.0F)).texOffs(0, 22).addBox(-1.6429F, 0.5F, -4.5F, 3.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(20, 17).addBox(-0.1429F, -3.5F, -2.5F, 0.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.1429F, 20.5F, 0.5F));
		piranha.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(0, 12).addBox(-2.0F, -1.0F, -5.0F, 4.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.1429F, 0.5F, -0.5F));
		piranha.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(20, 1).addBox(0.0F, -1.5F, 0.0F, 0.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.1429F, 0.0F, 2.5F));
		piranha.addOrReplaceChild("right_fin", CubeListBuilder.create().texOffs(20, 9).addBox(-2.0F, 0.0F, -1.0F, 2.0F, 0.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.6429F, 0.5F, 1.5F));
		piranha.addOrReplaceChild("left_fin", CubeListBuilder.create().texOffs(20, 11).addBox(0.0F, 0.0F, -1.0F, 2.0F, 0.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(1.3571F, 0.5F, 1.5F));
		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	@Override
	public void setupAnim(PiranhaEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		root().getAllParts().forEach(ModelPart::resetPose);
		animate(entity.getFlop(), PiranhaAnimations.flop,ageInTicks);
		animate(entity.getBite(),PiranhaAnimations.bite,ageInTicks);
		animate(entity.getFlop2(), PiranhaAnimations.flop2,ageInTicks);
		animate(entity.getSwim(),PiranhaAnimations.swim,ageInTicks);
		if (entity.isAggressive() && entity.getLastAttackTick() > 10) {
			jaw.offsetRotation(new Vector3f(45,0,0));
		}
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int i, int i1, int i2) {
		root().render(poseStack, vertexConsumer, i, i1, i2);
	}

	@Override
	public ModelPart root() {
		return all;
	}
}