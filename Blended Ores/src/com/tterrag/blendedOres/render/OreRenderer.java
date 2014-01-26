package com.tterrag.blendedOres.render;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;

import com.tterrag.blendedOres.BlendedOres;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class OreRenderer extends RenderBlocks implements ISimpleBlockRenderingHandler
{

	private int[] xyz;

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer)
	{
		renderer.renderBlockAsItem(Block.stone, modelID, 1.0F);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
	{
		Block renderBlock = findNearestDiffBlock(world, x, y, z, block);
		Icon icon1 = renderBlock.getBlockTexture(world, xyz[0], xyz[1], xyz[2], 0);
		if (renderBlock.blockMaterial == Material.rock && renderBlock.isOpaqueCube())
		{
			renderer.setOverrideBlockTexture(icon1);
		}
		if (block.blockID == Block.oreNetherQuartz.blockID && (renderBlock.blockID == Block.stone.blockID || renderBlock.blockMaterial != Material.rock || !renderBlock.isOpaqueCube()))
		{
			renderer.setOverrideBlockTexture(Block.netherrack.getIcon(0, 0));
		}
		renderer.renderStandardBlock(Block.stone, x, y, z);
		Icon icon2 = BlendedOres.proxy.getBlockTexture(world, x, y, z, world.getBlockMetadata(x, y, z));
		renderer.setOverrideBlockTexture(icon2);
		renderer.renderStandardBlock(Block.stone, x, y, z);
		renderer.clearOverrideBlockTexture();
		return false;
	}

	@Override
	public boolean shouldRender3DInInventory()
	{
		return true;
	}

	@Override
	public int getRenderId()
	{
		return BlendedOres.renderID;
	}

	private Block findNearestDiffBlock(IBlockAccess world, int xOrig, int yOrig, int zOrig, Block block)
	{
		if (world.isAirBlock(xOrig, yOrig, zOrig))
			return Block.stone;
		int[] dirs = new int[6];
		int x = xOrig, y = yOrig, z = zOrig;
		if (anyDiffBlocksTouching(world, xOrig, yOrig, zOrig, block))
		{
			if (world.getBlockId(x + 1, y, z) != block.blockID && !world.isAirBlock(x + 1, y, z))
				++x;
			else if (world.getBlockId(x - 1, y, z) != block.blockID && !world.isAirBlock(x - 1, y, z))
				--x;
			else if (world.getBlockId(x, y, z + 1) != block.blockID && !world.isAirBlock(x, y, z + 1))
				++z;
			else if (world.getBlockId(x, y, z - 1) != block.blockID && !world.isAirBlock(x, y, z - 1))
				--z;
			else if (world.getBlockId(x, y + 1, z) != block.blockID && !world.isAirBlock(x, y + 1, z))
				++y;
			else if (world.getBlockId(x, y - 1, z) != block.blockID && !world.isAirBlock(x, y - 1, z))
				--y;

			xyz = new int[] { x, y, z };
			return Block.blocksList[world.getBlockId(x, y, z)];
		}
		else
			return findNearestDiffBlock(world, x, y - 1, z, block);
	}

	private boolean anyDiffBlocksTouching(IBlockAccess world, int x, int y, int z, Block block)
	{
		return isDifferent(block, x + 1, y, z, world) || isDifferent(block, x - 1, y, z, world) || isDifferent(block, x, y + 1, z, world) || isDifferent(block, x, y - 1, z, world)
				|| isDifferent(block, x, y, z + 1, world) || isDifferent(block, x, y, z - 1, world);
	}

	private boolean isDifferent(Block block, int x, int y, int z, IBlockAccess world)
	{
		return world.getBlockId(x, y, z) != block.blockID && !world.isAirBlock(x, y, z);
	}

	@Override
	public Icon getBlockIcon(Block block, IBlockAccess world, int x, int y, int z, int par6)
	{
		return BlendedOres.proxy.getBlockTexture(world, x, y, z, world.getBlockMetadata(x, y, z));
	}
}
