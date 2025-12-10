package com.desx.datagen;

import com.desx.ModBlocks;
import com.desx.primitive.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        // Рецепт: Кирпичи буквой V и земля в центре
        ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, ModBlocks.BONSAI_POT)
                .pattern("B B")
                .pattern(" B ")
                .pattern(" D ")
                .input('B', Items.BRICK)
                .input('D', Items.DIRT)
                .criterion(FabricRecipeProvider.hasItem(Items.BRICK),
                        FabricRecipeProvider.conditionsFromItem(Items.BRICK))
                .offerTo(exporter);


        // 1. Веревка из травы
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.PLANT_TWINE)
                .pattern("G")
                .pattern("G")
                .pattern("G")
                .input('G', ModItems.DRY_GRASS)
                .criterion(FabricRecipeProvider.hasItem(ModItems.DRY_GRASS), FabricRecipeProvider.conditionsFromItem(ModItems.DRY_GRASS))
                .offerTo(exporter);

        // 2. Примитивный нож
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, ModItems.PRIMITIVE_KNIFE)
                .pattern(" F")
                .pattern("TS")
                .input('F', ModItems.FLINT_SHARD)
                .input('T', ModItems.PLANT_TWINE)
                .input('S', Items.STICK)
                .criterion(FabricRecipeProvider.hasItem(ModItems.FLINT_SHARD), FabricRecipeProvider.conditionsFromItem(ModItems.FLINT_SHARD))
                .offerTo(exporter);

        // 3. Палочка для розжига (Палка + Трава)
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, ModItems.IGNITION_STICK)
                .pattern("S")
                .pattern("G")
                .input('S', Items.STICK)
                .input('G', ModItems.DRY_GRASS)
                .criterion(FabricRecipeProvider.hasItem(Items.STICK), FabricRecipeProvider.conditionsFromItem(Items.STICK))
                .offerTo(exporter);

    }


}