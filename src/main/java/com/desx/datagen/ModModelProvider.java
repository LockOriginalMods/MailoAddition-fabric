package com.desx.datagen;

import com.desx.MailoAddition;
import com.desx.ModBlocks;

import com.desx.primitive.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.*;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        // --- BONSAI POT (Бонсай) ---
        TextureKey POT_KEY = TextureKey.of("flowerpot");
        TextureKey DIRT_KEY = TextureKey.of("dirt");
        TextureKey PARTICLE_KEY = TextureKey.PARTICLE;

        Model potModelTemplate = new Model(
                Optional.of(Identifier.of("minecraft", "block/flower_pot")),
                Optional.empty(),
                POT_KEY, DIRT_KEY, PARTICLE_KEY
        );

        TextureMap potTextures = new TextureMap()
                .put(POT_KEY, Identifier.of(MailoAddition.MOD_ID, "block/bonsai_pot_side"))
                .put(DIRT_KEY, Identifier.of(MailoAddition.MOD_ID, "block/bonsai_pot_top"))
                .put(PARTICLE_KEY, Identifier.of(MailoAddition.MOD_ID, "block/bonsai_pot_side"));

        Identifier modelId = potModelTemplate.upload(com.desx.ModBlocks.BONSAI_POT, potTextures, blockStateModelGenerator.modelCollector);

        blockStateModelGenerator.blockStateCollector.accept(
                BlockStateModelGenerator.createSingletonBlockState(com.desx.ModBlocks.BONSAI_POT, modelId)
        );

        // --- FIRE PIT (Костровая яма) ---
        // ИСПРАВЛЕНИЕ: Используем модель КОВРА (Carpet), так как она плоская.
        // TexturedModel.CARPET автоматически ищет текстуру с именем блока (fire_pit) и натягивает её на модель ковра.
        blockStateModelGenerator.registerSingleton(ModBlocks.FIRE_PIT, TexturedModel.CARPET);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        // Бонсай
        itemModelGenerator.register(com.desx.ModBlocks.BONSAI_POT.asItem(),
                new Model(Optional.of(Identifier.of(MailoAddition.MOD_ID, "block/bonsai_pot")), Optional.empty()));

        // Новые предметы Эры 1
        itemModelGenerator.register(ModItems.FLINT_SHARD, Models.GENERATED);
        itemModelGenerator.register(ModItems.DRY_GRASS, Models.GENERATED);
        itemModelGenerator.register(ModItems.PLANT_TWINE, Models.GENERATED);
        itemModelGenerator.register(ModItems.PRIMITIVE_KNIFE, Models.HANDHELD);
        itemModelGenerator.register(ModItems.IGNITION_STICK, Models.HANDHELD);
    }
}