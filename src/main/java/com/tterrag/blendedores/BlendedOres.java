package com.tterrag.blendedores;

import com.tterrag.blendedores.config.ConfigHandler;
import com.tterrag.blendedores.event.TextureEvents;
import com.tterrag.blendedores.lib.Reference;
import com.tterrag.blendedores.proxy.CommonProxy;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION, dependencies = "after:arsmagica2")
public class BlendedOres {

	@Instance(Reference.MOD_ID)
	public static BlendedOres instance;
	
	@SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
    public static CommonProxy proxy;
	
	public static int renderID;
		
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new TextureEvents());
		ConfigHandler.init(event.getSuggestedConfigurationFile());
		System.out.println("RenderID: " + renderID);
		proxy.initRenderers();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
	    ConfigHandler.postInit();
	}
}
