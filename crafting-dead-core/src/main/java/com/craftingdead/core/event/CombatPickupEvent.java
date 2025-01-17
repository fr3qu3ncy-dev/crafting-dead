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

package com.craftingdead.core.event;

import javax.annotation.Nullable;
import com.craftingdead.core.world.item.combatslot.CombatSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Cancelable
public class CombatPickupEvent extends Event {

  private final ItemStack itemStack;
  @Nullable
  private final CombatSlot combatSlot;

  public CombatPickupEvent(ItemStack itemStack, CombatSlot combatSlot) {
    this.itemStack = itemStack;
    this.combatSlot = combatSlot;
  }

  public ItemStack getItemStack() {
    return this.itemStack;
  }

  public CombatSlot getCombatSlot() {
    return this.combatSlot;
  }
}
