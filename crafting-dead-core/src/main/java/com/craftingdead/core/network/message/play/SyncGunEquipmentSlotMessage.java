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

package com.craftingdead.core.network.message.play;

import java.util.function.Supplier;
import com.craftingdead.core.network.NetworkUtil;
import com.craftingdead.core.world.item.gun.Gun;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkEvent;

public record SyncGunEquipmentSlotMessage(int entityId, EquipmentSlot slot, FriendlyByteBuf data) {

  public SyncGunEquipmentSlotMessage(int entityId, EquipmentSlot slot, Gun gun,
      boolean writeAll) {
    this(entityId, slot, new FriendlyByteBuf(Unpooled.buffer()));
    gun.encode(this.data, writeAll);
  }

  public void encode(FriendlyByteBuf out) {
    out.writeVarInt(this.entityId);
    out.writeEnum(this.slot);
    out.writeVarInt(this.data.readableBytes());
    out.writeBytes(this.data);
  }

  public static SyncGunEquipmentSlotMessage decode(FriendlyByteBuf in) {
    int entityId = in.readVarInt();
    EquipmentSlot slot = in.readEnum(EquipmentSlot.class);
    byte[] data = new byte[in.readVarInt()];
    in.readBytes(data);
    return new SyncGunEquipmentSlotMessage(entityId, slot,
        new FriendlyByteBuf(Unpooled.wrappedBuffer(data)));
  }

  public boolean handle(Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> NetworkUtil.getEntity(ctx.get(), this.entityId, LivingEntity.class)
        .getItemBySlot(this.slot)
        .getCapability(Gun.CAPABILITY)
        .ifPresent(gun -> gun.decode(this.data)));
    return true;
  }
}
