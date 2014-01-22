package com.tterrag.blendedOres.render;

import com.tterrag.blendedOres.BlendedOres;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class OreRenderer implements ISimpleBlockRenderingHandler{

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID,
			RenderBlocks renderer) {
		renderer.renderBlockAsItem(Block.stone, modelID, 1.0F);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z,
			Block block, int modelId, RenderBlocks renderer) {
		Block renderBlock = findNearestDiffBlock(world, x, y, z, block);
		renderer.renderStandardBlock(renderBlock == null || !renderBlock.isOpaqueCube() ? Block.stone : renderBlock, x, y, z);
		renderer.renderFaceXNeg(block, x, y, z, BlendedOres.proxy.getBlockTexture(world, x, y, z, world.getBlockMetadata(x, y, z)));
		return false;
	}

	@Override
	public boolean shouldRender3DInInventory() {
		return true;
	}

	@Override
	public int getRenderId() {
		return BlendedOres.renderID;
	}
	
	private Block findNearestDiffBlock(IBlockAccess world, int x, int y, int z, Block block)
	{
		if (world.isAirBlock(x, y, z))
			return Block.stone;
		int[] dirs = new int[6];
		if (anyDiffBlocksTouching(world, x, y, z, block))
		{
			 if (world.getBlockId(x + 1, y, z) != block.blockID && !world.isAirBlock(x + 1, y, z))
				 return Block.blocksList[world.getBlockId(x + 1, y, z)];
			 if (world.getBlockId(x - 1, y, z) != block.blockID && !world.isAirBlock(x - 1, y, z))
				 return Block.blocksList[world.getBlockId(x - 1, y, z)];
			 if (world.getBlockId(x, y + 1, z) != block.blockID && !world.isAirBlock(x, y + 1, z))
				 return Block.blocksList[world.getBlockId(x, y + 1, z)];
			 if (world.getBlockId(x, y - 1, z) != block.blockID && !world.isAirBlock(x, y - 1, z))
				 return Block.blocksList[world.getBlockId(x, y - 1, z)];
			 if (world.getBlockId(x, y, z + 1) != block.blockID && !world.isAirBlock(x, y, z + 1))
				 return Block.blocksList[world.getBlockId(x, y, z + 1)];
			 if (world.getBlockId(x, y, z - 1) != block.blockID && !world.isAirBlock(x, y, z - 1))
				 return Block.blocksList[world.getBlockId(x, y, z - 1)];
		}
		else return findNearestDiffBlock(world, x, y - 1, z, block);
		return Block.stone;
	}
	
	private boolean anyDiffBlocksTouching(IBlockAccess world, int x, int y, int z, Block block)
	{
		return world.getBlockId(x + 1, y, z) != block.blockID
				|| world.getBlockId(x - 1, y, z) != block.blockID
				|| world.getBlockId(x, y + 1, z) != block.blockID
				|| world.getBlockId(x, y - 1, z) != block.blockID
				|| world.getBlockId(x, y, z + 1) != block.blockID
				|| world.getBlockId(x, y, z - 1) != block.blockID;
	}

}
