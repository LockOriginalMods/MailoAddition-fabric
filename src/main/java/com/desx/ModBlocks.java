package com.desx;

import com.desx.primitive.block.FirePitBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks {

    // ИСПРАВЛЕНИЕ ЗДЕСЬ:
    // Мы передаем настройки (Settings) внутрь new BonsaiPotBlock(...)
    public static final Block BONSAI_POT = registerBlock("bonsai_pot",
            new BonsaiPotBlock(AbstractBlock.Settings.create()
                    .nonOpaque() // Блок не полный (прозрачный фон)
                    .strength(1.0f) // Прочность как у камня/горшка
            ));

    public static final Block FIRE_PIT = registerBlock("fire_pit",
            new FirePitBlock(AbstractBlock.Settings.create().noCollision().breakInstantly()));

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, Identifier.of(MailoAddition.MOD_ID, name), block);
    }

    private static void registerBlockItem(String name, Block block) {
        Registry.register(Registries.ITEM, Identifier.of(MailoAddition.MOD_ID, name),
                new BlockItem(block, new Item.Settings()));
    }

    public static void registerModBlocks() {
        MailoAddition.LOGGER.info("Registering ModBlocks for " + MailoAddition.MOD_ID);
    }
}