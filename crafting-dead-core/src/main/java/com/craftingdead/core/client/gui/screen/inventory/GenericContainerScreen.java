/*
 * Crafting Dead
 * Copyright (C) 2022  NexusNode LTD
 *
 * This Non-Commercial Software License Agreement (the "Agreement") is made between you (the "Licensee") and NEXUSNODE (BRAD HUNTER). (the "Licensor").
 * By installing or otherwise using Crafting Dead (the "Software"), you agree to be bound by the terms and conditions of this Agreement as may be revised from time to time at Licensor's sole discretion.
 *
 * If you do not agree to the terms and conditions of this Agreement do not download, copy, reproduce or otherwise use any of the source code available online at any time.
 *
 * https://github.com/nexusnode/crafting-dead/blob/1.18.x/LICENSE.txt
 *
 * https://craftingdead.net/terms.php
 */

package com.craftingdead.core.client.gui.screen.inventory;

import com.craftingdead.core.world.inventory.AbstractMenu;
import com.craftingdead.core.world.inventory.GenericMenu;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;

public class GenericContainerScreen extends AbstractContainerScreen<GenericMenu> {

  private static final ResourceLocation GENERIC_CONTAINER_TEXTURE =
      new ResourceLocation("textures/gui/container/generic_54.png");

  private static final int TITLE_TEXT_COLOUR = 0x404040;

  public GenericContainerScreen(GenericMenu menu, Inventory playerInventory,
      Component title) {
    super(menu, playerInventory, title);
    this.passEvents = false;
    this.imageHeight = 114 + this.menu.getRows() * AbstractMenu.SLOT_SIZE;
  }

  @Override
  public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    super.render(matrixStack, mouseX, mouseY, partialTicks);
    this.renderTooltip(matrixStack, mouseX, mouseY);
  }

  @Override
  protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
    this.font.draw(poseStack, this.title, 8.0F, 6.0F, 4210752);
    this.font.draw(poseStack, this.playerInventoryTitle, 8.0F,
        this.imageHeight - 96 + 2, TITLE_TEXT_COLOUR);
  }

  @Override
  protected void renderBg(PoseStack poseStack, float partialTicks, int mouseX, int mouseY) {
    this.renderBackground(poseStack);
    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    RenderSystem.setShaderTexture(0, GENERIC_CONTAINER_TEXTURE);
    int x = (this.width - this.imageWidth) / 2;
    int y = (this.height - this.imageHeight) / 2;
    this.blit(poseStack, x, y, 0, 0, this.imageWidth,
        this.menu.getRows() * AbstractMenu.SLOT_SIZE + AbstractMenu.SLOT_SIZE - 1);
    this.blit(poseStack, x,
        y + (this.menu.getRows() * AbstractMenu.SLOT_SIZE) + AbstractMenu.SLOT_SIZE - 1, 0,
        7 * AbstractMenu.SLOT_SIZE, this.imageWidth, 96);
  }
}
