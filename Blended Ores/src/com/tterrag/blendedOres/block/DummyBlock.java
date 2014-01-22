package com.tterrag.blendedOres.block;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.Icon;

import com.tterrag.blendedOres.BlendedOres;

public class DummyBlock extends Block
{

	public DummyBlock(int id)
	{
		super(id, Material.rock);
	}
	
	@Override
	public Icon getIcon(int par1, int par2)
	{
		ArrayList<Integer> idAndMeta = new ArrayList<Integer>();
		idAndMeta.add(par1);
		idAndMeta.add(par2);
		return BlendedOres.proxy.icons.get(BlendedOres.proxy.idAndMetaToRenderID.get(idAndMeta));
	}

}
