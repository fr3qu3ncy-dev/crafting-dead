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

package com.craftingdead.immerse.world.action;

import java.util.function.Supplier;
import com.craftingdead.core.world.action.item.ItemActionType;
import com.craftingdead.core.world.entity.extension.LivingExtension;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;

public class BuildDoorWallActionType extends BuildActionType {

  private final Supplier<Block> wallBlock;
  private final Supplier<Block> doorBlock;

  protected BuildDoorWallActionType(Builder builder) {
    super(builder);
    this.wallBlock = builder.wallBlock;
    this.doorBlock = builder.doorBlock;
  }

  public Block getWallBlock() {
    return this.wallBlock.get();
  }

  public Block getDoorBlock() {
    return this.doorBlock.get();
  }

  @Override
  protected BuildAction create(LivingExtension<?, ?> performer, BlockPlaceContext context) {
    return new BuildDoorWallAction(performer, context, this);
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder extends BuildActionType.Builder<Builder> {

    private Supplier<Block> wallBlock;
    private Supplier<Block> doorBlock;

    public Builder wallBlock(Block wallBlock) {
      return this.wallBlock(() -> wallBlock);
    }

    public Builder wallBlock(Supplier<Block> wallBlock) {
      this.wallBlock = wallBlock;
      return this;
    }

    public Builder doorBlock(Block doorBlock) {
      return this.doorBlock(() -> doorBlock);
    }

    public Builder doorBlock(Supplier<Block> doorBlock) {
      this.doorBlock = doorBlock;
      return this;
    }

    @Override
    public ItemActionType<?> build() {
      return new BuildDoorWallActionType(this);
    }
  }
}
