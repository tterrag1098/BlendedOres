package com.tterrag.blendedOres.proxy;

import java.util.ArrayList;
import java.util.HashMap;

import com.tterrag.blendedOres.lib.Reference;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.event.TextureStitchEvent;

public class CommonProxy {

	public HashMap<ArrayList<Integer>, Integer> idAndMetaToRenderID;
	public ArrayList<Icon> icons;
	public ArrayList<int[]> idsAndMetasToRender;
	
	public void initSounds() {
		
	}

	public void initRenderers(TextureStitchEvent event) {
		
		idsAndMetasToRender = new ArrayList<int[]>();
		idsAndMetasToRender.add(new int[]{14, 0});
		idsAndMetasToRender.add(new int[]{15, 0});
		idsAndMetasToRender.add(new int[]{16, 0});
		idsAndMetasToRender.add(new int[]{21, 0});
		idsAndMetasToRender.add(new int[]{56, 0});
		idsAndMetasToRender.add(new int[]{73, 0});
		idsAndMetasToRender.add(new int[]{74, 0});
		idsAndMetasToRender.add(new int[]{129, 0});
		idsAndMetasToRender.add(new int[]{153, 0});

		
		idAndMetaToRenderID = new HashMap<ArrayList<Integer>, Integer>();
		icons = new ArrayList<Icon>();
		
		int idx = 0;
		for (int[] i : idsAndMetasToRender)
		{
			
			ArrayList<Integer> toAdd = new ArrayList<Integer>();
			for (Integer integer : i)
			{
				toAdd.add(integer);
			}
			idAndMetaToRenderID.put(toAdd, idx);
			idx++;
		}
		
		IconRegister register = new IconRegister()
		{
			@Override
			public Icon registerIcon(String s)
			{
				// TODO Auto-generated method stub
				return null;
			}
		};
		
		icons.add(event.map.registerIcon(Reference.MOD_ID + ":gold"));
		icons.add(event.map.registerIcon(Reference.MOD_ID + ":iron"));
		icons.add(event.map.registerIcon(Reference.MOD_ID + ":coal"));
		icons.add(event.map.registerIcon(Reference.MOD_ID + ":lapis"));
		icons.add(event.map.registerIcon(Reference.MOD_ID + ":diamond"));
		icons.add(event.map.registerIcon(Reference.MOD_ID + ":redStone"));
		icons.add(event.map.registerIcon(Reference.MOD_ID + ":redStone"));
		icons.add(event.map.registerIcon(Reference.MOD_ID + ":emerald"));
		icons.add(event.map.registerIcon(Reference.MOD_ID + ":quartz"));
	}

	public Icon getBlockTexture(IBlockAccess world, int x, int y, int z, int blockMetadata)
	{
		ArrayList<Integer> idAndMeta = new ArrayList<Integer>();
		
		idAndMeta.add(world.getBlockId(x, y, z));
		idAndMeta.add(world.getBlockMetadata(x, y, x));
		
		if (idAndMetaToRenderID.get(idAndMeta) != null)
			return icons.get(idAndMetaToRenderID.get(idAndMeta));
		else return Block.bookShelf.getIcon(0, 0);
	}

}
