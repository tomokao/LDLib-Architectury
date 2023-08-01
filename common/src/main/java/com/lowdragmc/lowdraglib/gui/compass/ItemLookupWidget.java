package com.lowdragmc.lowdraglib.gui.compass;

import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import org.jetbrains.annotations.NotNull;


/**
 * @author KilaBash
 * @date 2023/7/30
 * @implNote ItemLookupWidget
 */
@Environment(EnvType.CLIENT)
public class ItemLookupWidget extends Widget {

    public ItemLookupWidget() {
        super(0, 0, Minecraft.getInstance().font.width(I18n.get("ldlib.compass.c_press")),10);
    }

    @Override
    public void drawInBackground(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        var pos = this.getPosition();
        var size = this.getSize();
        ColorPattern.WHITE.borderTexture(-1).draw(graphics, mouseX, mouseY, pos.x, pos.y, size.width, size.height);
        ColorPattern.GREEN.rectTexture().draw(graphics, mouseX, mouseY, pos.x + 2, pos.y + 2, (int)((size.width - 4) * CompassManager.INSTANCE.getCHoverProgress()), size.height - 4);
    }
}