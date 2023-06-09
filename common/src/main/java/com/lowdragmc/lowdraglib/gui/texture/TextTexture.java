package com.lowdragmc.lowdraglib.gui.texture;

import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.gui.editor.annotation.*;
import com.lowdragmc.lowdraglib.gui.util.DrawerHelper;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.utils.LocalizationUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@LDLRegister(name = "text_texture", group = "texture")
public class TextTexture extends TransformTexture{

    @Configurable
    public String text;

    @Configurable
    @NumberColor
    public int color;

    @Configurable
    @NumberColor
    public int backgroundColor;

    @Configurable(tips = "ldlib.gui.editor.tips.image_text_width")
    @NumberRange(range = {1, Integer.MAX_VALUE})
    public int width;
    @Configurable
    public boolean dropShadow;

    @Configurable(tips = "ldlib.gui.editor.tips.image_text_type")
    public TextType type;

    public Supplier<String> supplier;
    @Environment(EnvType.CLIENT)
    private List<String> texts;

    private long lastTick;

    public TextTexture() {
        this("A", -1);
        setWidth(50);
    }

    public TextTexture(String text, int color) {
        this.color = color;
        this.type = TextType.NORMAL;
        if (LDLib.isClient()) {
            this.text = LocalizationUtils.format(text);
            texts = Collections.singletonList(this.text);
        }
    }

    public TextTexture(String text) {
        this(text, -1);
        setDropShadow(true);
    }

    public TextTexture setSupplier(Supplier<String> supplier) {
        this.supplier = supplier;
        return this;
    }

    @Override
    public void updateTick() {
        if (Minecraft.getInstance().level != null) {
            long tick = Minecraft.getInstance().level.getGameTime();
            if (tick == lastTick) return;
            lastTick = tick;
        }
        if (supplier != null) {
            updateText(supplier.get());
        }
    }

    @ConfigSetter(field = "text")
    public void updateText(String text) {
        if (LDLib.isClient()) {
            this.text = LocalizationUtils.format(text);
            texts = Collections.singletonList(this.text);
            setWidth(this.width);
        }
    }

    public TextTexture setBackgroundColor(int color) {
        this.backgroundColor = color;
        return this;
    }

    public TextTexture setColor(int color) {
        this.color = color;
        return this;
    }

    public TextTexture setDropShadow(boolean dropShadow) {
        this.dropShadow = dropShadow;
        return this;
    }

    public TextTexture setWidth(int width) {
        this.width = width;
        if (LDLib.isClient()) {
            if (this.width > 0) {
                texts = Minecraft.getInstance()
                        .font.getSplitter()
                        .splitLines(text, width, Style.EMPTY)
                        .stream().map(FormattedText::getString)
                        .collect(Collectors.toList());
                if (texts.size() == 0) {
                    texts = Collections.singletonList(text);
                }
            } else {
                texts = Collections.singletonList(text);
            }
        }
        return this;
    }

    public TextTexture setType(TextType type) {
        this.type = type;
        return this;
    }

    @Environment(EnvType.CLIENT)
    @Override
    protected void drawInternal(GuiGraphics graphics, int mouseX, int mouseY, float x, float y, int width, int height) {
        if (backgroundColor != 0) {
            DrawerHelper.drawSolidRect(graphics, (int) x, (int) y, width, height, backgroundColor);
        }
        graphics.pose().pushPose();
        graphics.pose().translate(0, 0, 400);
        Font fontRenderer = Minecraft.getInstance().font;
        int textH = fontRenderer.lineHeight;
        if (type == TextType.NORMAL) {
            textH *= texts.size();
            for (int i = 0; i < texts.size(); i++) {
                String resultText = texts.get(i);
                int textW = fontRenderer.width(resultText);
                float _x = x + (width - textW) / 2f;
                float _y = y + (height - textH) / 2f + i * fontRenderer.lineHeight;
                graphics.drawString(fontRenderer, resultText, (int) _x, (int) _y, color, dropShadow);
            }
        } else if (type == TextType.HIDE) {
            int i = -1;
            if (Widget.isMouseOver((int) x, (int) y, width, height, mouseX, mouseY)) {
                i = (int) (Math.abs(System.currentTimeMillis() / 1000) % texts.size());
            }
            String resultText = i >= 0 ? texts.get(i) : (texts.get(0) + (texts.size() > 1 ? ".." : ""));
            drawTextLine(graphics, x, y, width, height, fontRenderer, textH, resultText);
        } else if (type == TextType.ROLL || type == TextType.ROLL_ALWAYS) {
            int i = 0;
            if (type == TextType.ROLL_ALWAYS || Widget.isMouseOver((int) x, (int) y, width, height, mouseX, mouseY)) {
                i = (int) (Math.abs(System.currentTimeMillis() / 1000) % texts.size());
            }
            String resultText = texts.get(i);
            drawTextLine(graphics, x, y, width, height, fontRenderer, textH, resultText);
        } else if (type == TextType.LEFT) {
            textH *= texts.size();
            for (int i = 0; i < texts.size(); i++) {
                String resultText = texts.get(i);
                float _y = y + (height - textH) / 2f + i * fontRenderer.lineHeight;
                graphics.drawString(fontRenderer, resultText, (int) x, (int) _y, color, dropShadow);
            }
        } else if (type == TextType.RIGHT) {
            textH *= texts.size();
            for (int i = 0; i < texts.size(); i++) {
                String resultText = texts.get(i);
                int textW = fontRenderer.width(resultText);
                float _y = y + (height - textH) / 2f + i * fontRenderer.lineHeight;
                graphics.drawString(fontRenderer, resultText, (int) (x + width - textW), (int) _y, color, dropShadow);
            }
        }
        graphics.pose().popPose();
        RenderSystem.setShaderColor(1, 1, 1, 1);
    }

    private void drawTextLine(GuiGraphics graphics, float x, float y, int width, int height, Font fontRenderer, int textH, String resultText) {
        int textW = fontRenderer.width(resultText);
        float _x = x + (width - textW) / 2f;
        float _y = y + (height - textH) / 2f;
        graphics.drawString(fontRenderer, resultText, (int) _x, (int) _y, color, dropShadow);
    }

    public enum TextType{
        NORMAL,
        HIDE,
        ROLL,
        ROLL_ALWAYS,
        LEFT,
        RIGHT
    }
}
