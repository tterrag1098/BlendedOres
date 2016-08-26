package com.tterrag.blendedores.client.model;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.tterrag.blendedores.config.ConfigHandler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Delegate;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BakedQuadRetextured;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.IModel;

@RequiredArgsConstructor
public class BlendedModel implements IBakedModel {

    private interface Exclusions {
        
        public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand);    
    }
            
    @Getter
    private final IModel source;
    
    @Delegate(excludes = Exclusions.class)
    private final IBakedModel base;
    
    @Setter
    private IBlockState baseState;
    
    @Setter
    private @Nonnull TextureAtlasSprite oreTexture = Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite(); // Lazy loaded from TextureStitchEvent
    
    @Setter
    @Getter
    private ResourceLocation oreTextureLocation;

    private ThreadLocal<IBlockAccess> cachedWorld = new ThreadLocal<>();
    private ThreadLocal<BlockPos> cachedPos = new ThreadLocal<>();
    
    public void preGetQuads(IBlockAccess world, BlockPos pos) {
        cachedWorld.set(world);
        cachedPos.set(pos);
    }
    
    @SuppressWarnings("null")
    @Override
    public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {

        IBlockAccess world = cachedWorld.get();
        BlockPos pos = cachedPos.get();
        IBlockState otherState = world.getBlockState(pos.down());

        if (otherState != baseState && ConfigHandler.validBlocks.contains(otherState)) {

            IBakedModel newBase = Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(otherState);
            if (newBase != null && !(newBase instanceof BlendedModel)) {

                List<BakedQuad> quads = newBase.getQuads(state, side, rand);
                if (MinecraftForgeClient.getRenderLayer() == BlockRenderLayer.CUTOUT) {
                    quads = quads.stream().map(q -> new BakedQuadRetextured(q, oreTexture)).collect(Collectors.toList());
                }
                return quads;
            }
        }
        
        return base.getQuads(state, side, rand);
    }
}
