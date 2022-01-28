/*
 * Crafting Dead
 * Copyright (C) 2021  NexusNode LTD
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.craftingdead.core.world.item;

import java.util.List;
import com.craftingdead.core.world.entity.grenade.Grenade;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class RemoteDetonatorItem extends Item {

  public static final int RANGE = 50;

  public RemoteDetonatorItem(Properties properties) {
    super(properties);
  }

  @Override
  public InteractionResultHolder<ItemStack> use(Level world, Player playerEntity,
      InteractionHand hand) {
    ItemStack itemstack = playerEntity.getItemInHand(hand);
    playerEntity.startUsingItem(hand);

    if (world instanceof ServerLevel) {
      ServerLevel serverWorld = (ServerLevel) world;

      serverWorld.playSound(null, playerEntity, SoundEvents.UI_BUTTON_CLICK,
          SoundSource.PLAYERS, 0.8F, 1.2F);

      serverWorld.getEntities(playerEntity,
          playerEntity.getBoundingBox().inflate(RANGE), (entity) -> {
            if (!(entity instanceof Grenade)) {
              return false;
            }
            Grenade grenadeEntity = (Grenade) entity;

            boolean isOwner =
                grenadeEntity.getThrower().map(thrower -> thrower == playerEntity).orElse(false);

            return isOwner && grenadeEntity.canBeRemotelyActivated();
          }).forEach(entity -> ((Grenade) entity).setActivated(true));
    }
    return InteractionResultHolder.consume(itemstack);
  }

  @Override
  public void appendHoverText(ItemStack stack, Level world,
      List<Component> lines, TooltipFlag tooltipFlag) {
    super.appendHoverText(stack, world, lines, tooltipFlag);
    lines.add(new TranslatableComponent("item_lore." + this.getRegistryName().getPath(), RANGE)
        .withStyle(ChatFormatting.GRAY));
  }
}
