package com.lowdragmc.lowdraglib.side.forge;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ContainerScreenEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;

/**
 * @author KilaBash
 * @date 2023/2/11
 * @implNote ForgeEventHooks
 */
public class ForgeEventHooksImpl {

    public static void postPlayerContainerEvent(Player player, AbstractContainerMenu container) {
        MinecraftForge.EVENT_BUS.post(new PlayerContainerEvent.Open(player, container));
    }

    @OnlyIn(Dist.CLIENT)
    public static void postBackgroundRenderedEvent(Screen screen, PoseStack poseStack) {
        RenderSystem.depthMask(true);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new ScreenEvent.BackgroundRendered(screen, poseStack));
        RenderSystem.depthMask(false);
    }

    @OnlyIn(Dist.CLIENT)
    public static void postRenderBackgroundEvent(AbstractContainerScreen<?> guiContainer, PoseStack poseStack, int mouseX, int mouseY) {
        RenderSystem.depthMask(true);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new ContainerScreenEvent.Render.Background(guiContainer, poseStack, mouseX, mouseY));
        RenderSystem.depthMask(false);
    }

    @OnlyIn(Dist.CLIENT)
    public static void postRenderForegroundEvent(AbstractContainerScreen<?> guiContainer, PoseStack poseStack, int mouseX, int mouseY) {
        RenderSystem.depthMask(true);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new ContainerScreenEvent.Render.Foreground(guiContainer, poseStack, mouseX, mouseY));
        RenderSystem.depthMask(false);
    }
}
