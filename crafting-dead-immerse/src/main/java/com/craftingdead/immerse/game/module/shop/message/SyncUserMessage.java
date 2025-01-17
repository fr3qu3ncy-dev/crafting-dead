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

package com.craftingdead.immerse.game.module.shop.message;

import net.minecraft.network.FriendlyByteBuf;

public class SyncUserMessage {

  private final int buyTimeSeconds;
  private final int money;

  public SyncUserMessage(int buyTimeSeconds, int money) {
    this.buyTimeSeconds = buyTimeSeconds;
    this.money = money;
  }

  public int getBuyTimeSeconds() {
    return this.buyTimeSeconds;
  }

  public int getMoney() {
    return this.money;
  }

  public void encode(FriendlyByteBuf out) {
    out.writeVarInt(this.buyTimeSeconds);
    out.writeVarInt(this.money);
  }

  public static SyncUserMessage decode(FriendlyByteBuf in) {
    return new SyncUserMessage(in.readVarInt(), in.readVarInt());
  }
}
