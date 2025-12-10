package com.desx;

import com.desx.block.entity.BonsaiPotBlockEntity;
import com.desx.primitive.block.entity.FirePitBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static BlockEntityType<BonsaiPotBlockEntity> BONSAI_POT_ENTITY;
    public static BlockEntityType<FirePitBlockEntity> FIRE_PIT_ENTITY;

    public static void registerBlockEntities() {
        BONSAI_POT_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE,
                Identifier.of(MailoAddition.MOD_ID, "bonsai_pot_entity"),
                FabricBlockEntityTypeBuilder.create(BonsaiPotBlockEntity::new, ModBlocks.BONSAI_POT).build(null));


        // В методе registerBlockEntities:
        FIRE_PIT_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE,
                Identifier.of(MailoAddition.MOD_ID, "fire_pit_entity"),
                FabricBlockEntityTypeBuilder.create(FirePitBlockEntity::new, ModBlocks.FIRE_PIT).build());
    }


}