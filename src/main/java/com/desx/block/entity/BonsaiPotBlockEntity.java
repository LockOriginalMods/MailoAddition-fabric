package com.desx.block.entity;

import com.desx.ModBlockEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BonsaiPotBlockEntity extends BlockEntity implements SidedInventory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(5, ItemStack.EMPTY);

    private int progress = 0;

    // ИЗМЕНЕНИЕ: 1200 тиков = 60 секунд (1 минута)
    private int maxProgress = 1200;

    public BonsaiPotBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BONSAI_POT_ENTITY, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, BonsaiPotBlockEntity entity) {
        if (world.isClient) return;

        ItemStack saplingStack = entity.inventory.get(0);

        if (!saplingStack.isEmpty()) {
            entity.progress++;
            if (entity.progress >= entity.maxProgress) {
                entity.progress = 0;
                entity.tryHarvest(saplingStack);
            }
        } else {
            entity.progress = 0;
        }
        // Синхронизация прогресса с клиентом каждые 20 тиков (чтобы анимация была плавной)
        if (entity.progress % 20 == 0) {
            world.updateListeners(pos, state, state, 3);
        }
    }

    private void tryHarvest(ItemStack saplingStack) {
        if (!(world instanceof ServerWorld serverWorld)) return;
        List<ItemStack> drops = generateDrops(saplingStack, serverWorld);
        for (ItemStack drop : drops) addToOutput(drop);
        markDirty();
        // Обновляем клиент, чтобы сбросить визуал роста
        world.updateListeners(pos, getCachedState(), getCachedState(), 3);
    }

    private List<ItemStack> generateDrops(ItemStack seed, ServerWorld world) {
        List<ItemStack> results = new java.util.ArrayList<>();

        if (seed.isIn(ItemTags.SAPLINGS)) {
            results.add(new ItemStack(Items.STICK, world.random.nextInt(2) + 1));
            if (seed.isOf(Items.OAK_SAPLING)) results.add(new ItemStack(Items.OAK_LOG));
            else if (seed.isOf(Items.SPRUCE_SAPLING)) results.add(new ItemStack(Items.SPRUCE_LOG));
            else if (seed.isOf(Items.BIRCH_SAPLING)) results.add(new ItemStack(Items.BIRCH_LOG));
            else if (seed.isOf(Items.JUNGLE_SAPLING)) results.add(new ItemStack(Items.JUNGLE_LOG));
            else if (seed.isOf(Items.ACACIA_SAPLING)) results.add(new ItemStack(Items.ACACIA_LOG));
            else if (seed.isOf(Items.DARK_OAK_SAPLING)) results.add(new ItemStack(Items.DARK_OAK_LOG));
            else if (seed.isOf(Items.CHERRY_SAPLING)) results.add(new ItemStack(Items.CHERRY_LOG));
            else if (seed.isOf(Items.MANGROVE_PROPAGULE)) results.add(new ItemStack(Items.MANGROVE_LOG));
            else results.add(seed.copy());
        }
        else if (seed.getItem() instanceof BlockItem || seed.getItem() instanceof AliasedBlockItem) {
            Block blockToPlant;
            if (seed.getItem() instanceof BlockItem bi) blockToPlant = bi.getBlock();
            else blockToPlant = ((AliasedBlockItem) seed.getItem()).getBlock();

            BlockState plantState = blockToPlant.getDefaultState();
            if (blockToPlant instanceof CropBlock crop) {
                plantState = crop.withAge(crop.getMaxAge());
            }

            LootTable lootTable = world.getServer().getReloadableRegistries().getLootTable(blockToPlant.getLootTableKey());
            LootContextParameterSet.Builder builder = new LootContextParameterSet.Builder(world)
                    .add(LootContextParameters.ORIGIN, Vec3d.ofCenter(pos))
                    .add(LootContextParameters.TOOL, ItemStack.EMPTY)
                    .add(LootContextParameters.BLOCK_STATE, plantState);

            results.addAll(lootTable.generateLoot(builder.build(LootContextTypes.BLOCK)));
        }
        return results;
    }

    private void addToOutput(ItemStack stack) {
        if (stack.isEmpty()) return;
        for (int i = 1; i < inventory.size(); i++) {
            ItemStack slot = inventory.get(i);
            if (slot.isEmpty()) {
                inventory.set(i, stack.copy());
                return;
            } else if (ItemStack.areItemsAndComponentsEqual(slot, stack)) {
                int available = slot.getMaxCount() - slot.getCount();
                int toAdd = Math.min(available, stack.getCount());
                slot.increment(toAdd);
                stack.decrement(toAdd);
                if (stack.isEmpty()) return;
            }
        }
    }

    // Геттеры для рендерера
    public int getProgress() { return progress; }
    public int getMaxProgress() { return maxProgress; }

    @Override
    public int size() { return inventory.size(); }
    @Override
    public boolean isEmpty() { return inventory.stream().allMatch(ItemStack::isEmpty); }
    @Override
    public ItemStack getStack(int slot) { return inventory.get(slot); }
    @Override
    public ItemStack removeStack(int slot, int amount) { return Inventories.splitStack(inventory, slot, amount); }
    @Override
    public ItemStack removeStack(int slot) { return Inventories.removeStack(inventory, slot); }
    @Override
    public void setStack(int slot, ItemStack stack) { inventory.set(slot, stack); markDirty(); }
    @Override
    public boolean canPlayerUse(net.minecraft.entity.player.PlayerEntity player) { return Inventory.canPlayerUse(this, player); }
    @Override
    public void clear() { inventory.clear(); }
    @Override
    public int[] getAvailableSlots(Direction side) { return side == Direction.DOWN ? new int[]{1, 2, 3, 4} : new int[]{0}; }
    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) { return slot == 0; }
    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) { return slot != 0; }
    public ItemStack getSapling() { return inventory.get(0); }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, inventory, registryLookup);
        nbt.putInt("bonsai.progress", progress);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        Inventories.readNbt(nbt, inventory, registryLookup);
        progress = nbt.getInt("bonsai.progress");
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }
}