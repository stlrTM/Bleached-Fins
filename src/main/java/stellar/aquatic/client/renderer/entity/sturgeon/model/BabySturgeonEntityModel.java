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

public class BabySturgeonEntityModel extends HierarchicalModel<SturgeonEntity> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(loc("baby_sturgeon"), "main");
	private final ModelPart bone;
	private final ModelPart Head;
	private final ModelPart RightFin;
	private final ModelPart LeftFin;
	private final ModelPart Tail;

	public BabySturgeonEntityModel(ModelPart root) {
		this.bone = root.getChild("bone");
		this.Head = this.bone.getChild("Head");
		this.RightFin = this.bone.getChild("RightFin");
		this.LeftFin = this.bone.getChild("LeftFin");
		this.Tail = this.bone.getChild("Tail");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(0, 0).addBox(-1.9286F, -2.2857F, -3.2857F, 4.0F, 4.0F, 7.0F, new CubeDeformation(0.0F))
		.texOffs(0, 11).addBox(0.0714F, -3.2857F, -3.2857F, 0.0F, 1.0F, 7.0F, new CubeDeformation(0.0F))
		.texOffs(12, 22).addBox(0.0714F, 1.7143F, 0.7143F, 0.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.0714F, 22.2857F, 0.2857F));

		PartDefinition Head = bone.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 19).addBox(-1.0F, -1.5F, -3.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(22, 5).addBox(0.0F, 0.5F, -6.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(22, 13).addBox(0.0F, 1.5F, -5.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.4286F, -0.2857F, -3.2857F));

		PartDefinition RightFin = bone.addOrReplaceChild("RightFin", CubeListBuilder.create().texOffs(22, 9).addBox(-2.0F, 0.0F, -1.0F, 2.0F, 0.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.9286F, 0.7143F, -1.2857F));

		PartDefinition LeftFin = bone.addOrReplaceChild("LeftFin", CubeListBuilder.create().texOffs(22, 11).addBox(0.0F, 0.0F, -1.0F, 2.0F, 0.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0714F, 0.7143F, -1.2857F));

		PartDefinition Tail = bone.addOrReplaceChild("Tail", CubeListBuilder.create().texOffs(22, 0).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(14, 12).addBox(0.0F, -4.0F, 2.0F, 0.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0714F, -0.2857F, 3.7143F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	@Override
	public void renderToBuffer(PoseStack p_170625_, VertexConsumer p_170626_, int p_170627_, int p_170628_, int p_350603_) {
		root().render(p_170625_, p_170626_, p_170627_, p_170628_, p_350603_);
	}

	@Override
	public ModelPart root() {
		return bone;
	}

	@Override
	public void setupAnim(SturgeonEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		root().getAllParts().forEach(ModelPart::resetPose);
		animate(entity.getFlop(), BabySturgeonEntityModelAnimations.flop,ageInTicks);
		animate(entity.getSwim(),BabySturgeonEntityModelAnimations.swim,ageInTicks);
	}
}