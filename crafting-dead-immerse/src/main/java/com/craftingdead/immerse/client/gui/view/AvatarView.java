package com.craftingdead.immerse.client.gui.view;

import com.craftingdead.immerse.client.gui.view.layout.Layout;
import com.craftingdead.immerse.client.util.RenderUtil;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;

public class AvatarView<L extends Layout> extends View<AvatarView<L>, L> {

  private ResourceLocation textureLocation;

  public AvatarView(L layout, GameProfile gameProfile) {
    super(layout);
    this.textureLocation = DefaultPlayerSkin.getDefaultSkin(gameProfile.getId());
    this.minecraft.getSkinManager().registerSkins(gameProfile,
        (type, textureLocation, texture) -> {
          if (type == MinecraftProfileTexture.Type.SKIN) {
            this.textureLocation = textureLocation;
          }
        }, true);
  }

  @Override
  protected void renderContent(PoseStack matrixStack, int mouseX, int mouseY,
      float partialTicks) {
    super.renderContent(matrixStack, mouseX, mouseY, partialTicks);
    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.getAlpha());
    RenderUtil.blitAvatar(this.textureLocation, matrixStack,
        this.getScaledContentX(), this.getScaledContentY(),
        this.getScaledContentWidth(), this.getScaledContentHeight());
    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
  }
}
