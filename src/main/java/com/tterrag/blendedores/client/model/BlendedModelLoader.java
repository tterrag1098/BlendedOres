package com.tterrag.blendedores.client.model;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import com.tterrag.blendedores.client.resource.DummyResourcePack;
import com.tterrag.blendedores.config.ConfigHandler;
import com.tterrag.blendedores.config.ConfigHandler.BlockInfo;
import com.tterrag.blendedores.config.ConfigHandler.Tolerance;

import lombok.SneakyThrows;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class BlendedModelLoader {
    
    private static final Map<ResourceLocation, BufferedImage> textureCache = Maps.newHashMap();

    @SubscribeEvent
    public void onModelBake(ModelBakeEvent event) {
        for (Entry<Block, BlockInfo> e : ConfigHandler.ores.entrySet()) {
            for (ModelResourceLocation res : e.getValue().getModels()) {
                IBakedModel model = event.getModelRegistry().getObject(res);
                if (!(model instanceof BlendedModel)) {
                    Map<ModelResourceLocation, IModel> stateModels = ReflectionHelper.getPrivateValue(ModelLoader.class, event.getModelLoader(), "stateModels");
                    IModel imodel = stateModels.get(res);
                    BlendedModel wrapped = new BlendedModel(imodel, model);
                    wrapped.setOreTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("minecraft:blocks/" + e.getValue().getTexture().getResourcePath() + "_differential"));
                    wrapped.setBaseState(e.getValue().getState());
                    event.getModelRegistry().putObject(res, wrapped);
                }
            }
        }
    }
    
    @SubscribeEvent
    @SneakyThrows
    public void onTextureStitch(TextureStitchEvent.Pre event) {
        for (Entry<Block, BlockInfo> entry : ConfigHandler.ores.entrySet()) {
            ResourceLocation base = entry.getValue().getTexture();
            ResourceLocation res = new ResourceLocation(base.getResourceDomain(), "blocks/" + base.getResourcePath());

            ResourceLocation baseTextureRes = entry.getValue().getBaseTexture();
            BufferedImage baseTexture = textureCache.computeIfAbsent(baseTextureRes, r -> {
                try {
                    return ImageIO.read(Minecraft.getMinecraft().getResourceManager()
                            .getResource(new ResourceLocation(baseTextureRes.getResourceDomain(), "textures/blocks/" + baseTextureRes.getResourcePath() + ".png")).getInputStream());
                } catch (IOException e) {
                    throw Throwables.propagate(e);
                }
            });

            BufferedImage oreTexture = ImageIO
                    .read(Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation(res.getResourceDomain(), "textures/" + res.getResourcePath() + ".png")).getInputStream());

            try {
                @Nonnull
                BufferedImage differential = computeDifference(baseTexture, oreTexture, entry.getValue().getTolerances());
                ImageIO.write(differential, "png", new File(".", res.getResourcePath().substring(res.getResourcePath().lastIndexOf('/'))+ ".png"));
                ResourceLocation generated = new ResourceLocation(res.toString() + "_differential");
                DummyResourcePack.INSTANCE.addImage(new ResourceLocation(generated.getResourceDomain(), "textures/" + generated.getResourcePath() + ".png"), differential);
                event.getMap().registerSprite(generated);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Error creating ore texture for " + res);
            }
        }
    }
    
    private @Nonnull BufferedImage computeDifference(BufferedImage base, BufferedImage compare, Tolerance tolerance) {
        Preconditions.checkArgument(base.getHeight() == compare.getHeight() && base.getWidth() == compare.getHeight(), "Cannot create ore texture when sizes differ! Fix your resource packs.");
        
        int w = base.getWidth();
        int h = base.getHeight();
        
        BufferedImage ret = new BufferedImage(w, h, compare.getType());

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                int baseColor = base.getRGB(x, y);
                int compareColor = compare.getRGB(x, y);
                if (similarColors(baseColor, compareColor, tolerance)) {
                    ret.setRGB(x, y, 0);
                } else {
                    ret.setRGB(x, y, compareColor);
                }
            }
        }
        
        return ret;
    }

    private boolean similarColors(int baseColor, int compareColor, Tolerance tolerance) {
        if (baseColor == compareColor) {
            return true;
        }
        
        int r1 = (baseColor >> 16) & 0xff;
        int g1 = (baseColor >> 8) & 0xff;
        int b1 = (baseColor) & 0xff;
        int r2 = (compareColor >> 16) & 0xff;
        int g2 = (compareColor >> 8) & 0xff;
        int b2 = (compareColor) & 0xff;
        
        // Grab hue values
        float[] hsv1 = Color.RGBtoHSB(r1, g1, b1, null);
        float[] hsv2 = Color.RGBtoHSB(r2, g2, b2, null);
        
        // Check that hue is within a tolerable range. This fixes vanilla ore textures having slightly different grays than the stone texture.
        return Math.abs(hsv1[0] - hsv2[0]) < tolerance.getHue() && Math.abs(hsv1[1] - hsv2[1]) < tolerance.getSat() && Math.abs(hsv1[2] - hsv2[2]) < tolerance.getBrt();
    }
}
