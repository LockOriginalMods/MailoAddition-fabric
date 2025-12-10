package com.desx;

import com.desx.client.BonsaiPotRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class MailoAdditionClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Чтобы горшок был прозрачным (стекло/модель) если нужно
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.BONSAI_POT, RenderLayer.getCutout());

        BlockEntityRendererFactories.register(ModBlockEntities.BONSAI_POT_ENTITY, BonsaiPotRenderer::new);
    }
}