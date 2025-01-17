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

package com.craftingdead.core.world.item.combatslot;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public enum CombatSlot implements CombatSlotProvider, StringRepresentable {

  PRIMARY("primary", true),
  SECONDARY("secondary", true),
  MELEE("melee", false),
  GRENADE("grenade", false) {
    @Override
    protected int getAvailableSlot(Inventory playerInventory, boolean ignoreEmpty) {
      int index = super.getAvailableSlot(playerInventory, false);
      return index == -1 ? 3 : index;
    }
  },
  EXTRA("extra", false);

  public static final Codec<CombatSlot> CODEC =
      StringRepresentable.fromEnum(CombatSlot::values, CombatSlot::byName);
  private static final Map<String, CombatSlot> BY_NAME = Arrays.stream(values())
      .collect(Collectors.toMap(CombatSlot::getSerializedName, Function.identity()));

  private final String name;
  private final boolean dropExistingItems;

  private CombatSlot(String name, boolean dropExistingItems) {
    this.name = name;
    this.dropExistingItems = dropExistingItems;
  }

  @Override
  public String getSerializedName() {
    return this.name;
  }

  @Override
  public CombatSlot getCombatSlot() {
    return this;
  }

  protected int getAvailableSlot(Inventory playerInventory, boolean ignoreEmpty) {
    for (int i = 0; i < 6; i++) {
      if ((ignoreEmpty || playerInventory.getItem(i).isEmpty()) && getSlotType(i) == this) {
        return i;
      }
    }
    return -1;
  }

  public boolean addToInventory(ItemStack itemStack, Inventory playerInventory,
      boolean ignoreEmpty) {
    int index = this.getAvailableSlot(playerInventory, ignoreEmpty);
    if (index == -1) {
      return false;
    }
    if (this.dropExistingItems && !playerInventory.getItem(index).isEmpty()) {
      ItemStack oldStack = playerInventory.removeItemNoUpdate(index);
      playerInventory.player.drop(oldStack, true, true);
    }
    playerInventory.setItem(index, itemStack);
    return true;
  }

  public static Optional<CombatSlot> getSlotType(ItemStack itemStack) {
    return itemStack.getCapability(CAPABILITY).map(CombatSlotProvider::getCombatSlot);
  }

  public boolean isItemValid(ItemStack itemStack) {
    return itemStack.isEmpty() || getSlotType(itemStack)
        .map(this::equals)
        .orElse(false);
  }

  public static boolean isInventoryValid(Inventory inventory) {
    for (int i = 0; i < 7; i++) {
      if (!CombatSlot
          .isItemValidForSlot(inventory.getItem(i), i)) {
        return false;
      }
    }
    return true;
  }

  public static boolean isItemValidForSlot(ItemStack itemStack, int slot) {
    return getSlotType(slot).isItemValid(itemStack);
  }

  public static CombatSlot getSlotType(int slot) {
    switch (slot) {
      case 0:
        return PRIMARY;
      case 1:
        return SECONDARY;
      case 2:
        return MELEE;
      case 3:
      case 4:
      case 5:
        return GRENADE;
      case 6:
        return EXTRA;
      default:
        throw new IllegalArgumentException("Invalid slot");
    }
  }

  public static CombatSlot byName(String name) {
    return BY_NAME.get(name);
  }
}
