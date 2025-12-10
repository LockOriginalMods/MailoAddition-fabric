package com.desx.primitive.block;

import com.desx.primitive.block.entity.FirePitBlockEntity;
import com.desx.primitive.item.ModItems;
import com.desx.ModBlockEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class FirePitBlock extends BlockWithEntity {
    public static final MapCodec<FirePitBlock> CODEC = createCodec(FirePitBlock::new);
    // Яма плоская, как ковер
    private static final VoxelShape SHAPE = Block.createCuboidShape(0, 0, 0, 16, 2, 16);
    public FirePitBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() { return CODEC; }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new FirePitBlockEntity(pos, state);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;

        BlockEntity entity = world.getBlockEntity(pos);
        if (entity instanceof FirePitBlockEntity firePit) {
            ItemStack stack = player.getMainHandStack();

            // 1. Кладем ресурсы (Трава -> Палки)
            if (firePit.addFuel(stack)) {
                return ActionResult.SUCCESS;
            }

            // 2. Мини-игра (Кликаем палочкой для розжига)
            if (stack.isOf(ModItems.IGNITION_STICK)) {
                firePit.tryIgnite(player);
                // Немного ломаем палочку (шанс)
                if (world.random.nextFloat() < 0.2f) {
                    stack.damage(1, player, net.minecraft.entity.EquipmentSlot.MAINHAND);
                }
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }
}