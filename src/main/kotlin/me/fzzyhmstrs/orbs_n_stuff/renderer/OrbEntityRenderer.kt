package me.fzzyhmstrs.orbs_n_stuff.renderer

import me.fzzyhmstrs.orbs_n_stuff.entity.OrbEntity
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.EntityRenderer
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.render.entity.model.EntityModelLayers
import net.minecraft.client.render.entity.model.ShulkerBulletEntityModel
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.projectile.ShulkerBulletEntity
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ColorHelper
import net.minecraft.util.math.MathHelper

class OrbEntityRenderer(ctx: EntityRendererFactory.Context) : EntityRenderer<OrbEntity>(ctx) {

    private val TEXTURE = Identifier.of("textures/entity/shulker/spark.png")
    private val LAYER = RenderLayer.getEntityTranslucent(TEXTURE)
    private val model: ShulkerBulletEntityModel<ShulkerBulletEntity> = ShulkerBulletEntityModel(ctx.getPart(EntityModelLayers.SHULKER_BULLET))

    override fun getBlockLight(shulkerBulletEntity: OrbEntity, blockPos: BlockPos): Int {
        return 15
    }

    override fun render(
        orbEntity: OrbEntity,
        f: Float,
        h: Float,
        matrixStack: MatrixStack,
        vertexConsumerProvider: VertexConsumerProvider,
        i: Int
    ) {
        matrixStack.push()
        val k = orbEntity.age.toFloat() + h
        matrixStack.translate(0.0, 0.15, 0.0)
        matrixStack.multiply(FzzyRotation.POSITIVE_Y.degrees(MathHelper.sin(k * 0.1f) * 180.0f))
        matrixStack.multiply(FzzyRotation.POSITIVE_X.degrees(MathHelper.cos(k * 0.1f) * 180.0f))
        matrixStack.multiply(FzzyRotation.POSITIVE_Z.degrees(MathHelper.sin(k * 0.15f) * 360.0f))
        val color = orbEntity.variant.color.get()
        matrixStack.scale(-0.66667f, -0.66667f, 0.66667f)
        val vertexConsumer = vertexConsumerProvider.getBuffer(model.getLayer(TEXTURE))
        model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, color)
        matrixStack.scale(1.2f, 1.2f, 1.2f)
        val vertexConsumer2 = vertexConsumerProvider.getBuffer(LAYER)
        model.render(matrixStack, vertexConsumer2, i, OverlayTexture.DEFAULT_UV, ColorHelper.Argb.withAlpha(0x26, color))
        matrixStack.pop()
        super.render(orbEntity, f, h, matrixStack, vertexConsumerProvider, i)
    }

    override fun getTexture(orbEntity: OrbEntity): Identifier {
        return TEXTURE
    }
}