package com.desx;

import com.desx.primitive.event.FirePitHandler;
import com.desx.primitive.event.KnappingHandler;
import com.desx.primitive.event.LootHandler;
import com.desx.primitive.item.ModItems;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MailoAddition implements ModInitializer {
	public static final String MOD_ID = "mailo-addition";


	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {


        LOGGER.info("Initializing Mailo Addition...");

        // ВАЖНО: Регистрируем блоки и предметы СНАЧАЛА
        ModBlocks.registerModBlocks();

        // ВАЖНО: Регистрируем сущности (BlockEntities) ПОТОМ
        ModBlockEntities.registerBlockEntities();
        ModItems.registerModItems();

        // Регистрация событий
        LootHandler.registerLootTables();
        KnappingHandler.registerEvents();
        FirePitHandler.registerEvents();
        LOGGER.info("Mailo Addition initialized!");
	}
}