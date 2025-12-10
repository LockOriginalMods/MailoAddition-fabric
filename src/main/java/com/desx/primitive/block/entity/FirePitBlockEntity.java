package com.desx.primitive.block.entity;

import com.desx.primitive.item.ModItems;
import com.desx.ModBlockEntities; // Убедись, что зарегистрировал этот тип позже!
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class FirePitBlockEntity extends BlockEntity {
    // Стадии костра
    public boolean hasGrass = false;
    public boolean hasSticks = false;

    // Мини-игра
    private int frictionHeat = 0;
    private long lastClickTime = 0;

    public FirePitBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FIRE_PIT_ENTITY, pos, state);
    }

    // Логика добавления ресурсов
    public boolean addFuel(ItemStack stack) {
        if (stack.isOf(ModItems.DRY_GRASS) && !hasGrass) {
            hasGrass = true;
            stack.decrement(1);
            markDirty();
            sync();
            return true;
        }
        if (stack.isOf(Items.STICK) && hasGrass && !hasSticks) {
            hasSticks = true;
            stack.decrement(1);
            markDirty();
            sync();
            return true;
        }
        return false;
    }

    // Мини-игра розжига
    public void tryIgnite(PlayerEntity player) {
        if (!hasGrass || !hasSticks) return;

        long currentTime = System.currentTimeMillis();

        // Если прошло меньше 300 мс с прошлого клика (быстрый клик)
        if (currentTime - lastClickTime < 300) {
            frictionHeat += 10; // Нагреваем
        } else {
            frictionHeat = Math.max(0, frictionHeat - 5); // Остывает, если медленно
        }
        lastClickTime = currentTime;

        // Визуальные эффекты
        if (world != null) {
            // Звук трения
            world.playSound(null, pos, SoundEvents.BLOCK_WOOD_STEP, SoundCategory.BLOCKS, 0.5f, 1.5f + (frictionHeat / 100f));
            // Частицы дыма
            world.addParticle(ParticleTypes.SMOKE, pos.getX() + 0.5, pos.getY() + 0.2, pos.getZ() + 0.5, 0, 0.05, 0);
        }

        // ПОБЕДА!
        if (frictionHeat >= 100) {
            igniteSuccess();
        }
    }

    private void igniteSuccess() {
        if (world == null) return;
        // Превращаем яму в настоящий костер
        world.setBlockState(pos, Blocks.CAMPFIRE.getDefaultState());
        world.playSound(null, pos, SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.BLOCKS, 1.0f, 1.0f);
    }

    // Синхронизация и NBT
    private void sync() {
        if (world != null) world.updateListeners(pos, getCachedState(), getCachedState(), 3);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putBoolean("hasGrass", hasGrass);
        nbt.putBoolean("hasSticks", hasSticks);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        hasGrass = nbt.getBoolean("hasGrass");
        hasSticks = nbt.getBoolean("hasSticks");
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