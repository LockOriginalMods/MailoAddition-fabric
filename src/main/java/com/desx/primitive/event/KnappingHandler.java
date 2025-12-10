package com.desx.primitive.event;

import com.desx.primitive.item.ModItems;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;

public class KnappingHandler {
    public static void registerEvents() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (world.isClient) return ActionResult.PASS;

            ItemStack stack = player.getStackInHand(hand);
            BlockPos pos = hitResult.getBlockPos();

            // Проверка: В руке Кремень?
            if (stack.isOf(Items.FLINT)) {
                // Проверка: Кликаем по твердому камню?
                if (world.getBlockState(pos).isOf(Blocks.STONE) || world.getBlockState(pos).isOf(Blocks.ANDESITE) || world.getBlockState(pos).isOf(Blocks.GRANITE)) {

                    // 1. Уменьшаем кремень в руке
                    if (!player.isCreative()) {
                        stack.decrement(1);
                    }

                    // 2. Звук удара камня
                    world.playSound(null, pos, SoundEvents.BLOCK_STONE_HIT, SoundCategory.BLOCKS, 1.0f, 1.5f);

                    // 3. Дропаем осколки
                    net.minecraft.block.Block.dropStack(world, pos.up(), new ItemStack(ModItems.FLINT_SHARD, 2));

                    return ActionResult.SUCCESS;
                }
            }

            return ActionResult.PASS;
        });
    }
}