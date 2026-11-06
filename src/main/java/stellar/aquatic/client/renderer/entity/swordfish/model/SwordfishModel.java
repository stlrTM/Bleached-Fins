package stellar.aquatic.client.renderer.entity.swordfish.model;// Made with Blockbench 5.1.4
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import stellar.aquatic.Aquatic;
import stellar.aquatic.content.entity.swordfish.SwordfishEntity;

import static stellar.aquatic.AquaticKt.loc;

public class SwordfishModel extends HierarchicalModel<SwordfishEntity> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(loc("swordfish"), "main");
	private final ModelPart rotation_code;
	private final ModelPart bone9;
	private final ModelPart bone5;
	private final ModelPart bone8;
	private final ModelPart bone6;
	private final ModelPart bone7;
	private final ModelPart bone3;
	private final ModelPart bone4;
	private final ModelPart bone;
	private final ModelPart bone2;

	public SwordfishModel(ModelPart root) {
		this.rotation_code = root.getChild("rotation_code");
		this.bone9 = this.rotation_code.getChild("bone9");
		this.bone5 = this.bone9.getChild("bone5");
		this.bone8 = this.bone5.getChild("bone8");
		this.bone6 = this.bone8.getChild("bone6");
		this.bone7 = this.bone6.getChild("bone7");
		this.bone3 = this.bone5.getChild("bone3");
		this.bone4 = this.bone3.getChild("bone4");
		this.bone = this.bone3.getChild("bone");
		this.bone2 = this.bone3.getChild("bone2");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition rotation_code = partdefinition.addOrReplaceChild("rotation_code", CubeListBuilder.create(), PartPose.offset(0.75F, 19.0F, 1.0F));

		PartDefinition bone9 = rotation_code.addOrReplaceChild("bone9", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition bone5 = bone9.addOrReplaceChild("bone5", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition bone8 = bone5.addOrReplaceChild("bone8", CubeListBuilder.create().texOffs(1, 25).addBox(-3.0F, -5.0F, 0.0F, 6.0F, 10.0F, 12.0F, new CubeDeformation(0.0F))
				.texOffs(59, 11).addBox(0.0F, 5.0F, 5.0F, 0.0F, 3.0F, 5.0F, new CubeDeformation(0.0F))
				.texOffs(10, 58).addBox(0.0F, -8.0F, 5.0F, 0.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.25F, -0.0698F, -0.0012F));

		PartDefinition bone6 = bone8.addOrReplaceChild("bone6", CubeListBuilder.create().texOffs(38, 39).addBox(-2.0F, -2.5F, 0.0F, 4.0F, 5.0F, 9.0F, new CubeDeformation(0.0F))
				.texOffs(19, 46).addBox(0.0F, 2.5F, 2.0F, 0.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
				.texOffs(0, 58).addBox(0.0F, -5.5F, 2.0F, 0.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.5F, 12.0F));

		PartDefinition bone7 = bone6.addOrReplaceChild("bone7", CubeListBuilder.create().texOffs(40, 53).addBox(0.0F, -5.5F, -1.0F, 0.0F, 11.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 9.0F));

		PartDefinition bone3 = bone5.addOrReplaceChild("bone3", CubeListBuilder.create().texOffs(2, 2).addBox(-2.5F, -5.0F, -14.0F, 6.0F, 10.0F, 12.0F, new CubeDeformation(0.0F))
				.texOffs(52, 50).addBox(0.5F, -12.0F, -12.0F, 0.0F, 7.0F, 8.0F, new CubeDeformation(0.0F))
				.texOffs(41, 12).addBox(0.5F, 5.0F, -10.0F, 0.0F, 3.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.25F, -0.0698F, 1.9988F));

		PartDefinition bone4 = bone3.addOrReplaceChild("bone4", CubeListBuilder.create().texOffs(40, 0).addBox(-2.0F, -2.0F, -7.0F, 4.0F, 5.0F, 7.0F, new CubeDeformation(0.0F))
				.texOffs(38, 24).addBox(-0.5F, -2.0F, -20.0F, 1.0F, 2.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offset(0.5F, 0.0F, -14.0F));

		PartDefinition bone = bone3.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(1, 48).addBox(0.0F, -1.0F, 0.0F, 0.0F, 2.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.5F, 3.0F, -7.0F, 0.0F, -0.0436F, 0.0F));

		PartDefinition bone2 = bone3.addOrReplaceChild("bone2", CubeListBuilder.create().texOffs(20, 53).addBox(0.0F, -1.0F, 0.0F, 0.0F, 2.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.5F, 3.0F, -8.0F, 0.0F, 0.0436F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(SwordfishEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		root().getAllParts().forEach(ModelPart::resetPose);
		if(entity.getEntityData().get(SwordfishEntity.Companion.getIS_RIDEN())) ageInTicks *= 2;
		animate(entity.getFlop(),SwordfishAnimations.flop,ageInTicks);
		animate(entity.getSwim(),SwordfishAnimations.swim,ageInTicks);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int i, int i1, int i2) {
		root().render(poseStack, vertexConsumer, i,i1,i2);
	}

	@Override
	public ModelPart root() {
		return rotation_code;
	}
}