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

package com.craftingdead.core.world.item.gun.magazine;

import com.craftingdead.core.network.Synched;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.INBTSerializable;

public interface Magazine extends INBTSerializable<CompoundTag>, Synched {
  
  Capability<Magazine> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

  float getArmorPenetration();

  int getSize();

  void setSize(int size);

  void refill();

  default boolean isEmpty() {
    return this.getSize() == 0;
  }

  int decrementSize();

  int getMaxSize();
}
