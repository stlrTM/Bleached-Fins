package stellar.aquatic.client.renderer.entity.sturgeon.model;// Made with Blockbench 5.1.4
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import stellar.aquatic.content.entity.sturgeon.SturgeonEntity;

import static stellar.aquatic.AquaticKt.loc;

public class SturgeonEntityModel extends HierarchicalModel<SturgeonEntity> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(loc("sturgeon"), "main");
	private final ModelPart all;
	private final ModelPart bone8;
	private final ModelPart bone5;
	private final ModelPart bone3;
	private final ModelPart bone9;
	private final ModelPart bone4;
	private final ModelPart bone6;
	private final ModelPart bone7;
	private final ModelPart bone2;

	public SturgeonEntityModel(ModelPart root) {
		this.all = root.getChild("all");
		this.bone8 = all.getChild("bone8");
		this.bone5 = this.bone8.getChild("bone5");
		this.bone3 = this.bone5.getChild("bone3");
		this.bone9 = this.bone3.getChild("bone9");
		this.bone4 = this.bone8.getChild("bone4");
		this.bone6 = this.bone4.getChild("bone6");
		this.bone7 = this.bone4.getChild("bone7");
		this.bone2 = this.bone4.getChild("bone2");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		PartDefinition all = partdefinition.addOrReplaceChild("all", CubeListBuilder.create(), PartPose.offset(0.0F, 0, 0.0F));

		PartDefinition bone8 = all.addOrReplaceChild("bone8", CubeListBuilder.create(), PartPose.offset(0.0F, 21.0F, -1.0F));

		PartDefinition bone5 = bone8.addOrReplaceChild("bone5", CubeListBuilder.create().texOffs(0, 0).addBox(-2.5F, -3.0F, 0.0F, 5.0F, 6.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(26, 32).addBox(0.0F, -4.0F, 0.0F, 0.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(18, 37).addBox(0.0F, 3.0F, 3.0F, 0.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition bone3 = bone5.addOrReplaceChild("bone3", CubeListBuilder.create().texOffs(24, 14).addBox(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 7.0F, new CubeDeformation(0.0F))
		.texOffs(26, 11).addBox(0.0F, -2.5F, 2.0F, 0.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(20, 27).addBox(0.0F, 1.5F, 2.0F, 0.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.5F, 8.0F));

		PartDefinition bone9 = bone3.addOrReplaceChild("bone9", CubeListBuilder.create().texOffs(0, 27).addBox(0.0F, -6.5F, -1.0F, 0.0F, 10.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 7.0F));

		PartDefinition bone4 = bone8.addOrReplaceChild("bone4", CubeListBuilder.create().texOffs(0, 14).addBox(-3.0F, -3.0F, -7.0F, 5.0F, 6.0F, 7.0F, new CubeDeformation(0.0F))
		.texOffs(8, 36).addBox(-0.5F, -4.0F, -5.0F, 0.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.5F, 0.0F, 0.0F));

		PartDefinition bone6 = bone4.addOrReplaceChild("bone6", CubeListBuilder.create().texOffs(28, 8).addBox(-4.0F, 0.0F, -1.5F, 4.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, 2.0F, -3.5F));

		PartDefinition bone7 = bone4.addOrReplaceChild("bone7", CubeListBuilder.create().texOffs(9, 33).addBox(0.0F, 0.0F, -1.5F, 4.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 2.0F, -3.5F));

		PartDefinition bone2 = bone4.addOrReplaceChild("bone2", CubeListBuilder.create().texOffs(24, 24).addBox(-2.5F, -2.0F, -4.0F, 5.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(26, 0).addBox(-1.5F, 0.0F, -9.0F, 3.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(8, 27).addBox(-1.5F, 2.0F, -7.0F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.5F, 0.0F, -7.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(SturgeonEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		root().getAllParts().forEach(ModelPart::resetPose);
		animate(entity.getFlop(),SturgeonAnimations.flop,ageInTicks);
		animate(entity.getFlop2(),SturgeonAnimations.flop2,ageInTicks);
		animate(entity.getSwim(),SturgeonAnimations.swim,ageInTicks);
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