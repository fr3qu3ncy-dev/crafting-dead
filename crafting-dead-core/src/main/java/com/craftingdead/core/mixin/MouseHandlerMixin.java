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

package com.craftingdead.core.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import com.craftingdead.core.world.entity.extension.LivingExtension;
import com.craftingdead.core.world.item.scope.Scope;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.Items;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {

  @Redirect(at = @At(value = "INVOKE",
      target = "Lnet/minecraft/client/player/LocalPlayer;isScoping()Z"),
      method = "turnPlayer")
  public boolean isScoping(LocalPlayer player) {
    var vanillaScoping = player.isUsingItem() && player.getUseItem().is(Items.SPYGLASS);
    return vanillaScoping || player.getCapability(LivingExtension.CAPABILITY)
        .resolve()
        .flatMap(living -> player.getMainHandItem().getCapability(Scope.CAPABILITY)
            .map(scope -> scope.isScoping(living) && scope.shouldReduceMouseSensitivity(living)))
        .orElse(false);
  }
}
