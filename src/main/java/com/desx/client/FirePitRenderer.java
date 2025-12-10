package com.desx.client;

import com.desx.primitive.block.entity.FirePitBlockEntity;
import com.desx.primitive.item.ModItems;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.RotationAxis;

public class FirePitRenderer implements BlockEntityRenderer<FirePitBlockEntity> {
    public FirePitRenderer(BlockEntityRendererFactory.Context ctx) {}

    @Override
    public void render(FirePitBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        // В методе render:
        matrices.push();

// Смещаем предметы почти к самому полу (0.05 по Y)
// Раньше было 0.1, опускаем ниже
        matrices.translate(0.5, 0.05, 0.5);

// Масштаб можно чуть увеличить, чтобы куча казалась объемной
        matrices.scale(0.6f, 0.6f, 0.6f);

        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));

// Рандомизация поворота (чтобы выглядело натурально)
// Можно использовать координаты блока для seed, чтобы поворот был статичным
        long seed = entity.getPos().asLong();
        float randomAngle = (seed % 360);
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(randomAngle));

// ... дальше отрисовка травы и палок ...
        // Если есть трава -> рисуем траву
        if (entity.hasGrass) {
            MinecraftClient.getInstance().getItemRenderer().renderItem(
                    new ItemStack(ModItems.DRY_GRASS),
                    ModelTransformationMode.FIXED, light, overlay, matrices, vertexConsumers, entity.getWorld(), 0
            );
        }

        // Если есть палки -> рисуем палки (чуть выше и повернуты)
        if (entity.hasSticks) {
            matrices.translate(0, 0, -0.1); // Чуть выше травы (по оси Z локально)
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(45)); // Крест накрест
            MinecraftClient.getInstance().getItemRenderer().renderItem(
                    new ItemStack(Items.STICK),
                    ModelTransformationMode.FIXED, light, overlay, matrices, vertexConsumers, entity.getWorld(), 0
            );
        }

        matrices.pop();
    }
}