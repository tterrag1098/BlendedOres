package com.tterrag.blendedores.proxy;

import com.tterrag.blendedores.client.model.BlendedModelLoader;
import com.tterrag.blendedores.client.resource.DummyResourcePack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy {
    
    @Override
    public void initRenderers() {
        DummyResourcePack.INSTANCE.inject();
        
        BlendedModelLoader loader = new BlendedModelLoader();
        MinecraftForge.EVENT_BUS.register(loader);
    }

}
