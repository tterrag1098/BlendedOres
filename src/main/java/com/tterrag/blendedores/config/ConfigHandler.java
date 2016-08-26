package com.tterrag.blendedores.config;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.tterrag.blendedores.client.resource.DummyResourcePack;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.Value;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class ConfigHandler {
    
    @Getter
    public static class Tolerance {
        private float hue = 0.05f;
        private float sat = 0.1f;
        private float brt = 0.15f;
    }
    
    @Getter
    @RequiredArgsConstructor
    private static class BlockAndState {
        private final ResourceLocation block;
        private Map<String, String> properties = Maps.newHashMap();
    }
    
    @Getter
    public static class BlockInfo {
        private List<ModelResourceLocation> models = null;
        private ResourceLocation texture = null;
        private ResourceLocation baseTexture = new ResourceLocation("stone");
        private BlockAndState base = new BlockAndState(baseTexture);

        private Tolerance tolerances = new Tolerance();
        @Getter(AccessLevel.NONE)
        
        private transient IBlockState stateObject;
        
        void postInit() {
            stateObject = parseState(ForgeRegistries.BLOCKS.getValue(base.getBlock()), base.getProperties());
        }
        
        public IBlockState getState() {
            return stateObject;
        }
    }
    
    @Value
    private static class ConfigData {
        private final Map<ResourceLocation, BlockInfo> ores;
        private final Map<ResourceLocation, List<Map<String, String>>> validBlocks; 
    }
    
    private static class ResourceLocationDeserializer implements JsonDeserializer<ResourceLocation> {

        @Override
        public ResourceLocation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json.isJsonPrimitive()) {
                String s = json.getAsString();
                if (s == null) {
                    return null;
                }
                return s.indexOf('#') != -1 ? new ModelResourceLocation(s) : new ResourceLocation(s);
            }
            return null;
        }
    }
    
    public static ConfigData configData;
    
    public static final Map<Block, BlockInfo> ores = Maps.newHashMap();
    public static final Set<IBlockState> validBlocks = Sets.newIdentityHashSet();

    @SneakyThrows
    public static void init(File file) {
        File jsonConfig = new File(file.getParentFile(), "blendedores.json");

        if (!jsonConfig.exists()) {
            jsonConfig.createNewFile();
        }
        
        configData = new GsonBuilder().registerTypeHierarchyAdapter(ResourceLocation.class, new ResourceLocationDeserializer()).create()
                .fromJson(new FileReader(jsonConfig), ConfigData.class);
        
        for (BlockInfo info : configData.getOres().values()) {
            DummyResourcePack.INSTANCE.addDomain(info.getTexture().getResourceDomain());
        }
    }
    
    public static void postInit() {
        for (Entry<ResourceLocation, BlockInfo> e : configData.getOres().entrySet()) {
            ores.put(ForgeRegistries.BLOCKS.getValue(e.getKey()), e.getValue());
        }
        for (Entry<Block, BlockInfo> e : ores.entrySet()) {
            e.getValue().postInit();
        }
        
        for (Entry<ResourceLocation, List<Map<String, String>>> e : configData.getValidBlocks().entrySet()) {
            Block block = ForgeRegistries.BLOCKS.getValue(e.getKey());
            parseStates(block, e.getValue(), validBlocks);
        }
    }
    
    private static void parseStates(Block block, List<Map<String, String>> states, Collection<IBlockState> data) {
        if (states.isEmpty()) {
            data.addAll(block.getBlockState().getValidStates());
        } else {
            for (Map<String, String> state : states) {
                data.add(parseState(block, state));
            }
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes", "null" })
    private static IBlockState parseState(Block block, Map<String, String> state) {
        IBlockState realstate = block.getDefaultState();
        BlockStateContainer container = block.getBlockState();
        for (Entry<String, String> e : state.entrySet()) {
            IProperty<?> prop = container.getProperty(e.getKey());
            if (prop != null) {
                Comparable<?> value = null;
                for (Comparable<?> obj : prop.getAllowedValues()) {
                    if (obj.equals(e.getValue())) {
                        value = obj;
                        break;
                    }
                }
                if (value != null) {
                    realstate = realstate.withProperty((IProperty) prop, (Comparable) value);
                }
            }
        }
        return realstate;
    }
}
