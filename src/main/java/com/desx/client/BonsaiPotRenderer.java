package com.desx.client;

import com.desx.block.entity.BonsaiPotBlockEntity;
import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;

public class BonsaiPotRenderer implements BlockEntityRenderer<BonsaiPotBlockEntity> {

    public BonsaiPotRenderer(BlockEntityRendererFactory.Context ctx) {}

    @Override
    public void render(BonsaiPotBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        ItemStack stack = entity.getSapling();
        if (stack.isEmpty()) return;

        BlockRenderManager blockRenderer = MinecraftClient.getInstance().getBlockRenderManager();
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getCutout());

        int maxProgress = entity.getMaxProgress() > 0 ? entity.getMaxProgress() : 1200;
        float progressPercent = (float) entity.getProgress() / maxProgress;
        progressPercent = Math.min(progressPercent, 1.0f);

        if (stack.isOf(Items.CHORUS_FLOWER) || stack.isOf(Items.CHORUS_FRUIT)) {
            renderChorus(entity, progressPercent, matrices, vertexConsumer, light, overlay, blockRenderer);
        }
        else if (stack.isIn(ItemTags.SAPLINGS) || stack.getItem().toString().contains("sapling")) {
            renderSmartTree(entity, stack, progressPercent, matrices, vertexConsumer, light, overlay, blockRenderer);
        }
        else {
            renderCrop(entity, stack, progressPercent, matrices, vertexConsumer, light, overlay, blockRenderer);
        }
    }

    // === УМНЫЙ РЕНДЕР ДЕРЕВЬЕВ ===
    private void renderSmartTree(BonsaiPotBlockEntity entity, ItemStack stack, float progress, MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, BlockRenderManager renderer) {
        matrices.push();
        matrices.translate(0.5, 0.375, 0.5);

        float scale = progress * 0.15f;
        matrices.scale(scale, scale, scale);

        // 1. Находим текстуры (блоки)
        BlockState[] materials = findTreeMaterials(stack);
        BlockState log = materials[0];
        BlockState leaves = materials[1];

        // 2. Определяем форму дерева по названию предмета
        String name = Registries.ITEM.getId(stack.getItem()).getPath().toLowerCase();

        if (name.contains("spruce") || name.contains("pine") || name.contains("fir") || name.contains("conifer") || name.contains("cypress") || name.contains("redwood")) {
            drawSpruceShape(matrices, renderer, vertexConsumer, entity, log, leaves);
        }
        else if (name.contains("birch") || name.contains("aspen") || name.contains("poplar")) {
            drawBirchShape(matrices, renderer, vertexConsumer, entity, log, leaves);
        }
        else if (name.contains("jungle") || name.contains("palm") || name.contains("rubber") || name.contains("mahogany")) {
            drawJungleShape(matrices, renderer, vertexConsumer, entity, log, leaves);
        }
        else if (name.contains("acacia") || name.contains("savanna") || name.contains("baobab")) {
            drawAcaciaShape(matrices, renderer, vertexConsumer, entity, log, leaves);
        }
        else if (name.contains("dark_oak") || name.contains("dark") || name.contains("shade")) {
            drawDarkOakShape(matrices, renderer, vertexConsumer, entity, log, leaves);
        }
        else if (name.contains("cherry") || name.contains("sakura") || name.contains("pink")) {
            drawCherryShape(matrices, renderer, vertexConsumer, entity, log, leaves);
        }
        else if (name.contains("mangrove") || name.contains("willow") || name.contains("swamp")) {
            drawMangroveShape(matrices, renderer, vertexConsumer, entity, log, leaves);
        }
        else {
            // По умолчанию (Дуб и все неизвестные)
            drawOakShape(matrices, renderer, vertexConsumer, entity, log, leaves);
        }

        matrices.pop();
    }

    // === ИСПРАВЛЕННЫЙ РЕНДЕР ХОРУСА ===
    private void renderChorus(BonsaiPotBlockEntity entity, float progress, MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, BlockRenderManager renderer) {
        matrices.push();
        matrices.translate(0.5, 0.375, 0.5);

        float scale = progress * 0.15f;
        matrices.scale(scale, scale, scale);

        // ВАЖНО: Включаем соединения (Connections), чтобы блоки соединялись
        BlockState stemBase = Blocks.CHORUS_PLANT.getDefaultState();
        BlockState flower = Blocks.CHORUS_FLOWER.getDefaultState().with(ChorusFlowerBlock.AGE, 5);

        // Вертикальная часть (соединена верх и низ)
        BlockState stemV = stemBase.with(ChorusPlantBlock.UP, true).with(ChorusPlantBlock.DOWN, true);
        // Нижняя часть (только верх)
        BlockState stemBottom = stemBase.with(ChorusPlantBlock.UP, true);
        // Тройник (верх, низ, право)
        BlockState stemBranchRight = stemBase.with(ChorusPlantBlock.UP, true).with(ChorusPlantBlock.DOWN, true).with(ChorusPlantBlock.EAST, true);
        BlockState stemBranchLeft = stemBase.with(ChorusPlantBlock.UP, true).with(ChorusPlantBlock.DOWN, true).with(ChorusPlantBlock.WEST, true);
        // Горизонтальные части
        BlockState stemEast = stemBase.with(ChorusPlantBlock.WEST, true).with(ChorusPlantBlock.EAST, true); // Соединение

        // Строим хорус
        addBlock(stemBottom, 0, 0, 0, matrices, renderer, vertexConsumer, entity);
        addBlock(stemV, 0, 1, 0, matrices, renderer, vertexConsumer, entity);

        // Развилка
        addBlock(stemBranchRight, 0, 2, 0, matrices, renderer, vertexConsumer, entity);
        addBlock(stemBase.with(ChorusPlantBlock.WEST, true).with(ChorusPlantBlock.UP, true), 1, 2, 0, matrices, renderer, vertexConsumer, entity);
        addBlock(flower, 1, 3, 0, matrices, renderer, vertexConsumer, entity); // Цветок справа

        addBlock(stemV, 0, 3, 0, matrices, renderer, vertexConsumer, entity);

        // Еще развилка влево
        addBlock(stemBranchLeft, 0, 4, 0, matrices, renderer, vertexConsumer, entity);
        addBlock(stemBase.with(ChorusPlantBlock.EAST, true).with(ChorusPlantBlock.UP, true), -1, 4, 0, matrices, renderer, vertexConsumer, entity);
        addBlock(flower, -1, 5, 0, matrices, renderer, vertexConsumer, entity); // Цветок слева

        addBlock(flower, 0, 5, 0, matrices, renderer, vertexConsumer, entity); // Цветок сверху

        matrices.pop();
    }

    // === ПОИСК ТЕКСТУР ДЛЯ МОДОВ ===
    private BlockState[] findTreeMaterials(ItemStack stack) {
        // Если это ваниль, возвращаем сразу готовые блоки
        if (stack.isOf(Items.OAK_SAPLING)) return new BlockState[]{Blocks.OAK_LOG.getDefaultState(), Blocks.OAK_LEAVES.getDefaultState()};
        if (stack.isOf(Items.SPRUCE_SAPLING)) return new BlockState[]{Blocks.SPRUCE_LOG.getDefaultState(), Blocks.SPRUCE_LEAVES.getDefaultState()};
        if (stack.isOf(Items.BIRCH_SAPLING)) return new BlockState[]{Blocks.BIRCH_LOG.getDefaultState(), Blocks.BIRCH_LEAVES.getDefaultState()};
        if (stack.isOf(Items.JUNGLE_SAPLING)) return new BlockState[]{Blocks.JUNGLE_LOG.getDefaultState(), Blocks.JUNGLE_LEAVES.getDefaultState()};
        if (stack.isOf(Items.ACACIA_SAPLING)) return new BlockState[]{Blocks.ACACIA_LOG.getDefaultState(), Blocks.ACACIA_LEAVES.getDefaultState()};
        if (stack.isOf(Items.DARK_OAK_SAPLING)) return new BlockState[]{Blocks.DARK_OAK_LOG.getDefaultState(), Blocks.DARK_OAK_LEAVES.getDefaultState()};
        if (stack.isOf(Items.CHERRY_SAPLING)) return new BlockState[]{Blocks.CHERRY_LOG.getDefaultState(), Blocks.CHERRY_LEAVES.getDefaultState()};
        if (stack.isOf(Items.MANGROVE_PROPAGULE)) return new BlockState[]{Blocks.MANGROVE_LOG.getDefaultState(), Blocks.MANGROVE_LEAVES.getDefaultState()};

        // Если это мод
        Identifier id = Registries.ITEM.getId(stack.getItem());
        String path = id.getPath();
        String namespace = id.getNamespace();

        String logName = path.replace("sapling", "log");
        String leavesName = path.replace("sapling", "leaves");

        // Обработка особых случаев имен (если слово sapling не найдено)
        if (logName.equals(path)) {
            logName = path + "_log";
            leavesName = path + "_leaves";
        }

        Block logBlock = Registries.BLOCK.get(Identifier.of(namespace, logName));
        Block leavesBlock = Registries.BLOCK.get(Identifier.of(namespace, leavesName));

        BlockState logState = (logBlock == Blocks.AIR) ? Blocks.OAK_LOG.getDefaultState() : logBlock.getDefaultState();
        BlockState leavesState = (leavesBlock == Blocks.AIR) ? Blocks.OAK_LEAVES.getDefaultState() : leavesBlock.getDefaultState();

        return new BlockState[]{logState, leavesState};
    }

    // === ФОРМЫ ДЕРЕВЬЕВ (ШАБЛОНЫ) ===

    private void drawOakShape(MatrixStack ms, BlockRenderManager rm, VertexConsumer vc, BonsaiPotBlockEntity be, BlockState log, BlockState leaves) {
        for(int y=0; y<5; y++) addBlock(log, 0, y, 0, ms, rm, vc, be);
        addBox(leaves, -2, 3, -2, 2, 4, 2, ms, rm, vc, be);
        addBox(leaves, -1, 5, -1, 1, 6, 1, ms, rm, vc, be);
    }

    private void drawBirchShape(MatrixStack ms, BlockRenderManager rm, VertexConsumer vc, BonsaiPotBlockEntity be, BlockState log, BlockState leaves) {
        for(int y=0; y<6; y++) addBlock(log, 0, y, 0, ms, rm, vc, be);
        addBox(leaves, -2, 3, -2, 2, 4, 2, ms, rm, vc, be);
        addBox(leaves, -1, 5, -1, 1, 6, 1, ms, rm, vc, be);
    }

    private void drawSpruceShape(MatrixStack ms, BlockRenderManager rm, VertexConsumer vc, BonsaiPotBlockEntity be, BlockState log, BlockState leaves) {
        for(int y=0; y<7; y++) addBlock(log, 0, y, 0, ms, rm, vc, be);
        addBox(leaves, -2, 2, -2, 2, 2, 2, ms, rm, vc, be);
        addBox(leaves, -1, 3, -1, 1, 3, 1, ms, rm, vc, be);
        addBox(leaves, -2, 4, -2, 2, 4, 2, ms, rm, vc, be);
        addBox(leaves, -1, 5, -1, 1, 5, 1, ms, rm, vc, be);
        addBox(leaves, 0, 6, 0, 0, 7, 0, ms, rm, vc, be);
    }

    private void drawJungleShape(MatrixStack ms, BlockRenderManager rm, VertexConsumer vc, BonsaiPotBlockEntity be, BlockState log, BlockState leaves) {
        for(int y=0; y<7; y++) addBlock(log, 0, y, 0, ms, rm, vc, be);
        addBox(leaves, -2, 5, -2, 2, 6, 2, ms, rm, vc, be);
        addBlock(leaves, 0, 7, 0, ms, rm, vc, be);
    }

    private void drawAcaciaShape(MatrixStack ms, BlockRenderManager rm, VertexConsumer vc, BonsaiPotBlockEntity be, BlockState log, BlockState leaves) {
        addBlock(log, 0, 0, 0, ms, rm, vc, be);
        addBlock(log, 0, 1, 0, ms, rm, vc, be);
        addBlock(log, 1, 2, 0, ms, rm, vc, be);
        addBlock(log, 2, 3, 0, ms, rm, vc, be);
        addBlock(log, 2, 4, 0, ms, rm, vc, be);
        addBlock(log, 0, 2, 1, ms, rm, vc, be);
        addBlock(log, 0, 3, 2, ms, rm, vc, be);
        addBox(leaves, 0, 4, -1, 3, 4, 2, ms, rm, vc, be);
        addBox(leaves, -1, 3, 1, 1, 3, 3, ms, rm, vc, be);
    }

    // === ИСПРАВЛЕННАЯ ВИШНЯ (Раскидистая и свисающая) ===
    private void drawCherryShape(MatrixStack ms, BlockRenderManager rm, VertexConsumer vc, BonsaiPotBlockEntity be, BlockState log, BlockState leaves) {
        // 1. Ствол (невысокий, но ветвистый)
        addBlock(log, 0, 0, 0, ms, rm, vc, be);
        addBlock(log, 0, 1, 0, ms, rm, vc, be);

        // Ветки в разные стороны (вишня часто раздваивается)
        addBlock(log, 1, 2, 0, ms, rm, vc, be);  // Вправо
        addBlock(log, -1, 2, 0, ms, rm, vc, be); // Влево
        addBlock(log, 0, 3, 1, ms, rm, vc, be);  // Назад и вверх

        // 2. Листва (Широкая и плоская)

        // Основной слой кроны (широкий блин)
        addBox(leaves, -2, 3, -1, 2, 3, 1, ms, rm, vc, be);
        addBox(leaves, -1, 3, -2, 1, 3, 2, ms, rm, vc, be);

        // Верхний слой (поменьше)
        addBox(leaves, -1, 4, -1, 1, 4, 1, ms, rm, vc, be);
        addBlock(leaves, 0, 5, 0, ms, rm, vc, be); // Макушка

        // 3. Свисающие "лепестки" (Самое важное для вишни!)
        // Добавляем блоки листвы НИЖЕ уровня веток по краям
        addBlock(leaves, 2, 2, 0, ms, rm, vc, be);
        addBlock(leaves, -2, 2, 0, ms, rm, vc, be);
        addBlock(leaves, 0, 2, 2, ms, rm, vc, be);
        addBlock(leaves, 0, 2, -2, ms, rm, vc, be);

        // Еще немного хаотичных свисаний
        addBlock(leaves, 1, 2, 1, ms, rm, vc, be);
        addBlock(leaves, -1, 2, -1, ms, rm, vc, be);
    }

    // === ИСПРАВЛЕННЫЙ ТЕМНЫЙ ДУБ (Центровка 2x2) ===
    private void drawDarkOakShape(MatrixStack ms, BlockRenderManager rm, VertexConsumer vc, BonsaiPotBlockEntity be, BlockState log, BlockState leaves) {
        ms.push();
        // ВАЖНО: Сдвигаем на -0.5, чтобы центр ствола 2x2 (стык 4 блоков)
        // совпал с центром горшка (0,0). Иначе дерево будет расти сбоку.
        ms.translate(-0.5, 0, -0.5);

        // 1. Мощный ствол 2x2
        for(int y=0; y<6; y++) {
            addBlock(log, 0, y, 0, ms, rm, vc, be);
            addBlock(log, 1, y, 0, ms, rm, vc, be);
            addBlock(log, 0, y, 1, ms, rm, vc, be);
            addBlock(log, 1, y, 1, ms, rm, vc, be);
        }

        // 2. Плотная, широкая шапка листвы
        // Нижний широкий слой
        addBox(leaves, -1, 3, -1, 2, 4, 2, ms, rm, vc, be); // Центр кроны
        addBox(leaves, -2, 3, -1, -2, 4, 2, ms, rm, vc, be); // Расширение влево
        addBox(leaves, 3, 3, -1, 3, 4, 2, ms, rm, vc, be);   // Расширение вправо
        addBox(leaves, -1, 3, -2, 2, 4, -2, ms, rm, vc, be); // Расширение назад
        addBox(leaves, -1, 3, 3, 2, 4, 3, ms, rm, vc, be);   // Расширение вперед

        // Средний слой
        addBox(leaves, -1, 5, -1, 2, 5, 2, ms, rm, vc, be);

        // Верхушка
        addBox(leaves, 0, 6, 0, 1, 6, 1, ms, rm, vc, be);

        ms.pop();
    }

    // === ИСПРАВЛЕННЫЙ МАНГР (НА КОРНЯХ) ===
    private void drawMangroveShape(MatrixStack ms, BlockRenderManager rm, VertexConsumer vc, BonsaiPotBlockEntity be, BlockState log, BlockState leaves) {
        // Нам обязательно нужны корни, а не бревно внизу
        BlockState roots = Blocks.MANGROVE_ROOTS.getDefaultState();

        // 1. КОРНЕВАЯ СИСТЕМА (Y = 0 и 1)
        // В ваниле мангры стоят на "паучьих" ножках.

        // Центральный корень (стержень)
        addBlock(roots, 0, 0, 0, ms, rm, vc, be);
        addBlock(roots, 0, 1, 0, ms, rm, vc, be);

        // "Ножки" по бокам (создают объем)
        addBlock(roots, 1, 0, 0, ms, rm, vc, be);
        addBlock(roots, -1, 0, 0, ms, rm, vc, be);
        addBlock(roots, 0, 0, 1, ms, rm, vc, be);
        addBlock(roots, 0, 0, -1, ms, rm, vc, be);

        // 2. СТВОЛ (Начинается выше корней)
        addBlock(log, 0, 2, 0, ms, rm, vc, be);
        addBlock(log, 0, 3, 0, ms, rm, vc, be);
        addBlock(log, 0, 4, 0, ms, rm, vc, be);

        // Ветвление (мангры часто кривые)
        addBlock(log, 1, 3, 0, ms, rm, vc, be); // Ветка вбок
        addBlock(log, -1, 4, 1, ms, rm, vc, be); // Ветка в другую сторону

        // 3. ЛИСТВА (Широкая и свисающая)

        // Основная шапка (широкая)
        addBox(leaves, -2, 4, -2, 2, 5, 2, ms, rm, vc, be);

        // Верхний слой
        addBox(leaves, -1, 6, -1, 1, 6, 1, ms, rm, vc, be);

        // Свисающая листва (характерно для мангров)
        // Свисает ниже уровня основной листвы, доходя до корней
        addBlock(leaves, 2, 3, 2, ms, rm, vc, be);
        addBlock(leaves, -2, 3, -2, ms, rm, vc, be);
        addBlock(leaves, 2, 3, -2, ms, rm, vc, be);
        addBlock(leaves, -2, 3, 2, ms, rm, vc, be);

        // Можно добавить еще один слой корней, если хочется, чтобы дерево казалось выше
        // Но для бонсая этого достаточно.
    }

    // === УТИЛИТЫ ===
    private void addBlock(BlockState state, int x, int y, int z, MatrixStack ms, BlockRenderManager rm, VertexConsumer vc, BonsaiPotBlockEntity be) {
        ms.push();
        ms.translate(x, y, z);
        ms.translate(-0.5, 0, -0.5);
        rm.renderBlock(state, be.getPos(), be.getWorld(), ms, vc, false, Random.create());
        ms.pop();
    }

    private void addBox(BlockState state, int x1, int y1, int z1, int x2, int y2, int z2, MatrixStack ms, BlockRenderManager rm, VertexConsumer vc, BonsaiPotBlockEntity be) {
        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                for (int z = z1; z <= z2; z++) {
                    addBlock(state, x, y, z, ms, rm, vc, be);
                }
            }
        }
    }

    private void renderCrop(BonsaiPotBlockEntity entity, ItemStack stack, float progress, MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, BlockRenderManager renderer) {
        BlockState stateToRender = null;
        if (stack.getItem() instanceof BlockItem blockItem) stateToRender = blockItem.getBlock().getDefaultState();
        else if (stack.getItem() instanceof AliasedBlockItem aliasedItem) stateToRender = aliasedItem.getBlock().getDefaultState();
        if (stateToRender == null) return;

        if (stateToRender.getBlock() instanceof CropBlock cropBlock) {
            int maxAge = cropBlock.getMaxAge();
            int currentAge = (progress >= 0.9f) ? maxAge : (int) (progress * maxAge);
            for (Property<?> prop : stateToRender.getProperties()) {
                if (prop instanceof IntProperty && prop.getName().equals("age")) {
                    stateToRender = stateToRender.with((IntProperty) prop, currentAge);
                    break;
                }
            }
        }
        matrices.push();
        matrices.translate(0.5, 0.375, 0.5);
        float scale = 0.3f;
        matrices.scale(scale, scale, scale);
        matrices.translate(-0.5, 0, -0.5);
        renderer.renderBlock(stateToRender, entity.getPos(), entity.getWorld(), matrices, vertexConsumer, false, Random.create());
        matrices.pop();
    }
}