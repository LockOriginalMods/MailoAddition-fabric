package com.desx.primitive.event;

import com.desx.primitive.item.ModItems;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.item.Items; // Не забудь этот импорт!
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.MatchToolLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.util.Identifier;

public class LootHandler {
    private static final Identifier GRASS_LOOT_TABLE_ID = Identifier.of("minecraft", "blocks/short_grass");
    private static final Identifier TALL_GRASS_LOOT_TABLE_ID = Identifier.of("minecraft", "blocks/tall_grass");
    private static final Identifier OAK_LEAVES_LOOT_TABLE_ID = Identifier.of("minecraft", "blocks/oak_leaves");

    public static void registerLootTables() {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {

            // 1. Трава -> Сухая трава
            if (GRASS_LOOT_TABLE_ID.equals(key.getValue()) || TALL_GRASS_LOOT_TABLE_ID.equals(key.getValue())) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .with(ItemEntry.builder(ModItems.DRY_GRASS))
                        .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0f)));

                tableBuilder.pool(poolBuilder);
            }

            // 2. Листва -> Палки
            if (OAK_LEAVES_LOOT_TABLE_ID.equals(key.getValue())) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .with(ItemEntry.builder(Items.STICK))
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 2.0f)))
                        // ИСПРАВЛЕНИЕ ЗДЕСЬ: Проверяем конкретный предмет Items.SHEARS вместо тега
                        .conditionally(MatchToolLootCondition.builder(ItemPredicate.Builder.create().items(Items.SHEARS)).invert());

                tableBuilder.pool(poolBuilder);
            }
        });
    }
}