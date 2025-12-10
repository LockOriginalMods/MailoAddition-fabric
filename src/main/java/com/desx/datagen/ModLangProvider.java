package com.desx.datagen;

import com.desx.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class ModLangProvider extends FabricLanguageProvider {
    public ModLangProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup); // По умолчанию генерирует en_us.json
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup registryLookup, TranslationBuilder translationBuilder) {
        translationBuilder.add(ModBlocks.BONSAI_POT, "Bonsai Pot");
            translationBuilder.add(ModBlocks.FIRE_PIT, "Fire Pit");

        // Если хочешь русский, создай отдельный класс наследующий этот
        // и передай "ru_ru" в конструктор super(dataOutput, "ru_ru", registryLookup);
    }
}