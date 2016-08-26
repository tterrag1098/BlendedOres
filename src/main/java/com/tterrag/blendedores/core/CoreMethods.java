package com.tterrag.blendedores.core;

import com.tterrag.blendedores.client.model.BlendedModel;
import com.tterrag.blendedores.config.ConfigHandler;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class CoreMethods {

    public static boolean canRenderInLayer(Block block, BlockRenderLayer layer) {
        return block.canRenderInLayer(layer) || (ConfigHandler.ores.containsKey(block) && layer == BlockRenderLayer.CUTOUT);
    }

    public static void preGetQuads(IBakedModel model, IBlockAccess world, BlockPos pos) {
        if (model instanceof BlendedModel) {
            ((BlendedModel)model).preGetQuads(world, pos);
        }
    }
}
