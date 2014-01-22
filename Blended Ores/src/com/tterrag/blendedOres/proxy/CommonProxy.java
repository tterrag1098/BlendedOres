package com.tterrag.blendedOres.proxy;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;

public class CommonProxy {

	public HashMap<ArrayList<Integer>, Integer> idAndMetaToRenderID;
	public ArrayList<Icon> icons;
	public ArrayList<int[]> idsAndMetasToRender = new ArrayList<int[]>();
	
	public void initSounds() {
		
	}

	public void initRenderers() {
		idsAndMetasToRender.add(new int[]{14, 0});
		idsAndMetasToRender.add(new int[]{15, 0});
		idsAndMetasToRender.add(new int[]{16, 0});
		idsAndMetasToRender.add(new int[]{21, 0});
		idsAndMetasToRender.add(new int[]{56, 0});
		idsAndMetasToRender.add(new int[]{73, 0});
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
		
		//TODO get transparent ore textures
		//icons.add(<first texture>);
		//icons.add(<second texture>);
		//etc.
	}

	public Icon getBlockTexture(IBlockAccess world, int x, int y, int z, int blockMetadata)
	{
		ArrayList<Integer> idAndMeta = new ArrayList<Integer>();
		
		idAndMeta.add(world.getBlockId(x, y, z));
		idAndMeta.add(world.getBlockMetadata(x, y, x));
		
		return icons.get(idAndMetaToRenderID.get(idAndMeta));
	}

}
