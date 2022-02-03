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

package com.craftingdead.immerse.client.gui.view;

import java.util.Collection;
import java.util.OptionalInt;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.Util;

public class States {

  private static final int NILL = -1;

  private static final Object2IntMap<String> states =
      Util.make(new Object2IntOpenHashMap<>(), map -> map.defaultReturnValue(NILL));

  public static final int ENABLED = register("enabled");
  public static final int DISABLED = register("disabled");
  public static final int HOVERED = register("hovered");
  public static final int SELECTED = register("selected");
  public static final int FOCUSED = register("focused");

  private static int n = 0;

  private static int register(String name) {
    var value = 1 << n++;
    states.put(name, value);
    return value;
  }

  public static OptionalInt getValue(String name) {
    var value = states.getInt(name);
    return value == NILL ? OptionalInt.empty() : OptionalInt.of(value);
  }

  public static int combine(int... states) {
    var combined = 0;
    for (int i = 0; i < states.length; i++) {
      combined |= states[i];
    }
    return combined;
  }

  public static int combine(Collection<Integer> collection) {
    var combined = 0;
    for (var state : collection) {
      combined |= state;
    }
    return combined;
  }

  public static IntSet split(int state) {
    var states = new IntOpenHashSet();
    States.states.values().forEach(s -> {
      if ((state & s) == s) {
        states.add(s);
      }
    });
    return states;
  }
}
