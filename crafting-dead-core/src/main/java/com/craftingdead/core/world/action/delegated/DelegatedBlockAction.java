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

package com.craftingdead.core.world.action.delegated;

import javax.annotation.Nullable;
import com.craftingdead.core.world.entity.extension.LivingExtension;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.ForgeMod;

public final class DelegatedBlockAction extends AbstractDelegatedAction<DelegatedBlockActionType> {

  private BlockPos blockPosTarget;
  private BlockState blockStateTarget;

  DelegatedBlockAction(DelegatedBlockActionType type) {
    super(type);
  }

  @Override
  public boolean canPerform(LivingExtension<?, ?> performer, @Nullable LivingExtension<?, ?> target,
      ItemStack heldStack) {
    AttributeInstance reachDistanceAttribute =
        performer.getEntity().getAttribute(ForgeMod.REACH_DISTANCE.get());
    HitResult result = performer.getEntity().pick(
        reachDistanceAttribute == null ? 4.0D : reachDistanceAttribute.getValue(), 1.0F, true);
    if (result instanceof BlockHitResult) {
      BlockPos blockPos = ((BlockHitResult) result).getBlockPos();
      BlockState blockState = performer.getLevel().getBlockState(blockPos);
      if (this.blockPosTarget == null || this.blockStateTarget == null) {
        this.blockPosTarget = blockPos;
        this.blockStateTarget = blockState;
        if (!this.type.getPredicate().test(this.blockStateTarget)) {
          return false;
        }
      }

      return this.blockPosTarget.equals(blockPos) && this.blockStateTarget.equals(blockState);
    }
    return false;
  }

  @Override
  public boolean finish(LivingExtension<?, ?> performer, LivingExtension<?, ?> target,
      ItemStack heldStack) {
    return true;
  }
}
