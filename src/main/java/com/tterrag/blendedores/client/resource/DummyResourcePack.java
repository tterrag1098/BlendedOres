package com.tterrag.blendedores.client.resource;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import lombok.SneakyThrows;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public enum DummyResourcePack implements IResourcePack {
    
    INSTANCE;
    
    private Map<ResourceLocation, BufferedImage> images = Maps.newHashMap();
    private Set<String> domains = Sets.newHashSet();

    @SneakyThrows
    public void addImage(ResourceLocation res, BufferedImage image) {
        images.put(res, image);
    }

    public void addDomain(String domain) {
        domains.add(domain);
    }

    public void inject() {
        List<IResourcePack> defaultResourcePacks = ReflectionHelper.getPrivateValue(Minecraft.class, Minecraft.getMinecraft(), "field_110449_ao",  "defaultResourcePacks");
        defaultResourcePacks.add(this);
    }
    
    @Override
    public InputStream getInputStream(ResourceLocation location) throws IOException {
        BufferedImage image = images.get(location);
        if (image != null) {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(image, "png", os);
            return new ByteArrayInputStream(os.toByteArray());
        }
        return null;
    }

    @Override
    public boolean resourceExists(ResourceLocation location) {
        return images.containsKey(location);
    }

    @Override
    public Set<String> getResourceDomains() {
        return ImmutableSet.copyOf(domains);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends IMetadataSection> T getPackMetadata(MetadataSerializer metadataSerializer, String metadataSectionName) throws IOException {
        return null;
    }

    @Override
    public BufferedImage getPackImage() throws IOException {
        return null;
    }

    @Override
    public String getPackName() {
        return "BlendedOres Textures";
    }
}
