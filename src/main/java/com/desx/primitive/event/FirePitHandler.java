package com.desx.primitive.event;

import com.desx.ModBlocks;
import com.desx.primitive.block.entity.FirePitBlockEntity;
import com.desx.primitive.item.ModItems;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class FirePitHandler {
    public static void registerEvents() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (world.isClient) return ActionResult.PASS;

            ItemStack stack = player.getStackInHand(hand);

            // Если в руке Сухая трава
            if (stack.isOf(ModItems.DRY_GRASS)) {
                BlockPos hitPos = hitResult.getBlockPos();
                BlockState hitState = world.getBlockState(hitPos);
                Direction side = hitResult.getSide();

                // Мы должны кликнуть по ВЕРХНЕЙ стороне блока
                if (side == Direction.UP) {
                    // Проверяем, что блок под низом "подходящий" (Земля, Камень, Гравий)
                    // Используем теги для совместимости
                    boolean isValidGround = hitState.isIn(BlockTags.DIRT) ||
                            hitState.isIn(BlockTags.BASE_STONE_OVERWORLD) ||
                            hitState.isIn(BlockTags.SAND);

                    if (isValidGround) {
                        BlockPos placePos = hitPos.up();
                        // Проверяем, что место для костра свободно (Воздух или Трава/Цветы)
                        if (world.getBlockState(placePos).isReplaceable()) {

                            // 1. Ставим наш блок Костровой Ямы
                            world.setBlockState(placePos, ModBlocks.FIRE_PIT.getDefaultState());

                            // 2. Сразу инициализируем его (кладем туда ту траву, которую держим)
                            if (world.getBlockEntity(placePos) instanceof FirePitBlockEntity firePit) {
                                firePit.hasGrass = true;
                                firePit.markDirty();
                            }

                            // 3. Убираем 1 траву из руки и играем звук
                            if (!player.isCreative()) {
                                stack.decrement(1);
                            }
                            world.playSound(null, placePos, SoundEvents.BLOCK_GRASS_PLACE, SoundCategory.BLOCKS, 1.0f, 0.8f);

                            return ActionResult.SUCCESS;
                        }
                    }
                }
            }
            return ActionResult.PASS;
        });
    }
}