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

package com.craftingdead.survival.world.entity.grenade;

import java.util.OptionalInt;
import com.craftingdead.core.particle.FlashParticleOptions;
import com.craftingdead.core.world.entity.ExplosionSource;
import com.craftingdead.core.world.entity.grenade.Grenade;
import com.craftingdead.core.world.item.GrenadeItem;
import com.craftingdead.survival.CraftingDeadSurvival;
import com.craftingdead.survival.world.entity.SurvivalEntityTypes;
import com.craftingdead.survival.world.item.SurvivalItems;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class PipeBomb extends Grenade implements ExplosionSource {

  private static final FlashParticleOptions RED_FLASH =
      new FlashParticleOptions(1F, 0.35F, 0.35F, 1F);

  public PipeBomb(EntityType<? extends Grenade> type, Level level) {
    super(type, level);
  }

  public PipeBomb(LivingEntity thrower, Level level) {
    super(SurvivalEntityTypes.PIPE_BOMB.get(), thrower, level);
  }

  @Override
  public OptionalInt getMinimumTicksUntilAutoActivation() {
    return OptionalInt.of(
        CraftingDeadSurvival.serverConfig.pipeBombTicksBeforeActivation.get());
  }

  @Override
  public void activatedChanged(boolean activated) {
    if (activated) {
      if (!this.level.isClientSide()) {
        this.kill();
        this.level.explode(this, this.createDamageSource(), null,
            this.getX(), this.getY() + this.getBbHeight(), this.getZ(),
            CraftingDeadSurvival.serverConfig.pipeBombRadius.get().floatValue(), false,
            CraftingDeadSurvival.serverConfig.pipeBombBlockInteraction.get());
      }
    }
  }

  @Override
  public void onGrenadeTick() {
    if (this.tickCount % 6 == 0) {
      if (this.level.isClientSide()) {
        this.level.addParticle(RED_FLASH, true,
            this.getX(), this.getY(), this.getZ(), 0D, 0D, 0D);
      } else {
        this.getMinimumTicksUntilAutoActivation().ifPresent(ticks -> {
          var pitchProgress = this.tickCount / (float) ticks;
          var gradualPitch = Mth.lerp(pitchProgress, 1.0F, 2F);
          this.playSound(SoundEvents.NOTE_BLOCK_BELL, 1.7F, gradualPitch);
        });
      }
    }
  }

  @Override
  public boolean isAttracting() {
    return true;
  }

  @Override
  public GrenadeItem asItem() {
    return SurvivalItems.PIPE_BOMB.get();
  }

  @Override
  public void onMotionStop(int stopsCount) {}

  @Override
  public float getDamageMultiplier() {
    return CraftingDeadSurvival.serverConfig.pipeBombDamageMultiplier.get().floatValue();
  }

  @Override
  public double getKnockbackMultiplier() {
    return CraftingDeadSurvival.serverConfig.pipeBombKnockbackMultiplier.get();
  }
}
