package com.desx.primitive.item;

import com.desx.MailoAddition;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {

    // Материалы
    public static final Item FLINT_SHARD = registerItem("flint_shard", new Item(new Item.Settings()));
    public static final Item DRY_GRASS = registerItem("dry_grass", new Item(new Item.Settings()));
    public static final Item PLANT_TWINE = registerItem("plant_twine", new Item(new Item.Settings())); // Веревка из травы

    // Инструменты (пока заглушки, реализуем логику позже)
    public static final Item PRIMITIVE_KNIFE = registerItem("primitive_knife", new Item(new Item.Settings().maxDamage(32)));
    // В классе ModItems:
    public static final Item IGNITION_STICK = registerItem("ignition_stick", new Item(new Item.Settings().maxDamage(10))); // Ломается при использовании

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(MailoAddition.MOD_ID, name), item);
    }

    public static void registerModItems() {
        MailoAddition.LOGGER.info("Registering Era 1 Items");
    }
}