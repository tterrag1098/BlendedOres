package com.tterrag.blendedOres.event;

import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.ForgeSubscribe;

import com.tterrag.blendedOres.BlendedOres;

public class TextureEvents
{
	@ForgeSubscribe
	public void onTextureStitch(TextureStitchEvent event)
	{
		if (event.map.textureType == 0)
			BlendedOres.proxy.initRenderers(event);
	}
}
