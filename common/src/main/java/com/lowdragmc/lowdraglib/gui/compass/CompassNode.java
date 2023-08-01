package com.lowdragmc.lowdraglib.gui.compass;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.json.SimpleIGuiTextureJsonUtils;
import com.lowdragmc.lowdraglib.utils.Position;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

/**
 * @author KilaBash
 * @date 2023/7/27
 * @implNote CompassNode
 */
@Accessors(chain = true)
public class CompassNode {
    @Getter
    protected final JsonObject config;
    @Getter
    protected final ResourceLocation nodeName;
    @Getter
    protected CompassSection section;
    @Getter
    protected Position position;
    @Getter
    protected int size;
    @Getter
    protected IGuiTexture buttonTexture;
    protected List<Item> items;

    public CompassNode(ResourceLocation nodeName, JsonObject config) {
        this.config = config;
        this.nodeName = nodeName;
        this.buttonTexture = SimpleIGuiTextureJsonUtils.fromJson(config.get("button_texture").getAsJsonObject());
        JsonArray position = config.get("position").getAsJsonArray();
        this.position = (new Position(position.get(0).getAsInt(), position.get(1).getAsInt()));
        this.size = GsonHelper.getAsInt(config, "size", 24);
    }

    public void setSection(CompassSection section) {
        this.section = section;
        this.section.addNode(this);
    }

    public void initRelation() {
        if (config.has("pre_nodes")) {
            JsonArray pre = config.get("pre_nodes").getAsJsonArray();
            List<CompassNode> preNodes = new ArrayList<>();
            for (JsonElement element : pre) {
                CompassNode node = section.getNode(new ResourceLocation(element.getAsString()));
                if (node != null) {
                    preNodes.add(node);
                }
            }
            section.addPreRelation(this, preNodes.toArray(CompassNode[]::new));
        }
    }

    @Override
    public final String toString() {
        return nodeName.toString();
    }

    public ResourceLocation getPage() {
        return new ResourceLocation(GsonHelper.getAsString(config, "page", "ldlib:missing"));
    }

    public List<Item> getItems() {
        if (items == null) {
            items = new ArrayList<>();
            JsonArray items = GsonHelper.getAsJsonArray(config, "items", new JsonArray());
            for (JsonElement element : items) {
                Item item = BuiltInRegistries.ITEM.get(new ResourceLocation(element.getAsString()));
                if (item != Items.AIR) {
                    this.items.add(item);
                }
            }
        }
        return items;
    }

    public Component getChatComponent() {
        return Component.translatable(nodeName.toLanguageKey("compass.node"));
    }
}