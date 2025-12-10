package com.desx;

import com.desx.block.entity.BonsaiPotBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ItemScatterer; // Для разбрасывания вещей
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BonsaiPotBlock extends BlockWithEntity {
    public static final MapCodec<BonsaiPotBlock> CODEC = createCodec(BonsaiPotBlock::new);
    private static final VoxelShape SHAPE = Block.createCuboidShape(4, 0, 4, 12, 6, 12);

    public BonsaiPotBlock(Settings settings) { super(settings); }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() { return CODEC; }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) { return SHAPE; }

    @Override
    public BlockRenderType getRenderType(BlockState state) { return BlockRenderType.MODEL; }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) { return new BonsaiPotBlockEntity(pos, state); }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;

        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof BonsaiPotBlockEntity bonsai) {
            ItemStack handStack = player.getMainHandStack();

            // Если рука пустая -> забираем результат (дроп)
            if (handStack.isEmpty()) {
                // Проходим по слотам выхода (1-4) и отдаем игроку
                boolean extracted = false;
                for (int i = 1; i < bonsai.size(); i++) {
                    ItemStack output = bonsai.getStack(i);
                    if (!output.isEmpty()) {
                        player.getInventory().offerOrDrop(output);
                        bonsai.setStack(i, ItemStack.EMPTY);
                        extracted = true;
                    }
                }

                // Если нет результата, забираем саженец
                if (!extracted && !bonsai.getSapling().isEmpty()) {
                    if (player.isSneaking()) { // Только если Shift+Клик чтобы случайно не выдернуть
                        player.getInventory().offerOrDrop(bonsai.getSapling());
                        bonsai.setStack(0, ItemStack.EMPTY);
                    }
                }
                return ActionResult.CONSUME;
            }
            // Если в руке предмет -> пытаемся посадить
            else if (bonsai.getSapling().isEmpty()) {
                ItemStack oneItem = handStack.copy();
                oneItem.setCount(1);
                bonsai.setStack(0, oneItem); // Кладем в слот 0

                if (!player.isCreative()) {
                    handStack.decrement(1);
                }
                return ActionResult.CONSUME;
            }
        }
        return ActionResult.PASS;
    }

    // ВАЖНО: Выбрасываем вещи при разрушении блока
    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof BonsaiPotBlockEntity) {
                ItemScatterer.spawn(world, pos, (BonsaiPotBlockEntity)blockEntity);
                world.updateComparators(pos, this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, ModBlockEntities.BONSAI_POT_ENTITY, BonsaiPotBlockEntity::tick);
    }
}