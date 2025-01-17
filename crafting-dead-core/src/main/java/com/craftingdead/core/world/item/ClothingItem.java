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

package com.craftingdead.core.world.item;

import java.util.List;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import com.craftingdead.core.capability.CapabilityUtil;
import com.craftingdead.core.world.item.clothing.Clothing;
import com.craftingdead.core.world.item.clothing.DefaultClothing;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class ClothingItem extends Item {

  private final Multimap<Attribute, AttributeModifier> attributeModifiers;
  private final boolean fireImmunity;

  public ClothingItem(Properties properties) {
    super(properties);
    this.attributeModifiers = properties.attributeModifiers.build();
    this.fireImmunity = properties.fireImmunity;
  }

  @Override
  public ICapabilityProvider initCapabilities(ItemStack itemStack, @Nullable CompoundTag nbt) {
    return CapabilityUtil.provider(
        () -> new DefaultClothing(
            this.attributeModifiers,
            this.fireImmunity,
            new ResourceLocation(this.getRegistryName().getNamespace(), "textures/clothing/"
                + this.getRegistryName().getPath() + "_" + "default" + ".png")),
        Clothing.CAPABILITY);
  }

  @Override
  public void appendHoverText(ItemStack stack, Level world, List<Component> lines,
      TooltipFlag tooltipFlag) {
    super.appendHoverText(stack, world, lines, tooltipFlag);

    if (this.fireImmunity) {
      lines.add(new TranslatableComponent("clothing.immune_to_fire")
          .withStyle(ChatFormatting.GRAY));
    }

    if (!this.attributeModifiers.isEmpty()) {
      lines.add(TextComponent.EMPTY);
      lines.add(
          new TranslatableComponent("item.modifiers.clothing").withStyle(ChatFormatting.GRAY));

      for (Entry<Attribute, AttributeModifier> entry : this.attributeModifiers.entries()) {
        AttributeModifier modifier = entry.getValue();
        double amount = modifier.getAmount();

        double multipliedAmount;
        if (modifier.getOperation() != AttributeModifier.Operation.MULTIPLY_BASE
            && modifier.getOperation() != AttributeModifier.Operation.MULTIPLY_TOTAL) {
          if (entry.getKey().equals(Attributes.KNOCKBACK_RESISTANCE)) {
            multipliedAmount = amount * 10.0D;
          } else {
            multipliedAmount = amount;
          }
        } else {
          multipliedAmount = amount * 100.0D;
        }

        if (modifier.getId() == Item.BASE_ATTACK_DAMAGE_UUID
            || modifier.getId() == Item.BASE_ATTACK_SPEED_UUID) {
          lines.add((new TextComponent(" "))
              .append(new TranslatableComponent(
                  "attribute.modifier.equals." + modifier.getOperation().toValue(),
                  ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(multipliedAmount),
                  new TranslatableComponent(entry.getKey().getDescriptionId())))
              .withStyle(ChatFormatting.DARK_GREEN));
        } else if (amount > 0.0D) {
          lines.add((new TranslatableComponent(
              "attribute.modifier.plus." + modifier.getOperation().toValue(),
              ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(multipliedAmount),
              new TranslatableComponent(entry.getKey().getDescriptionId())))
                  .withStyle(ChatFormatting.BLUE));
        } else if (amount < 0.0D) {
          multipliedAmount = multipliedAmount * -1.0D;
          lines.add((new TranslatableComponent(
              "attribute.modifier.take." + modifier.getOperation().toValue(),
              ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(multipliedAmount),
              new TranslatableComponent(entry.getKey().getDescriptionId())))
                  .withStyle(ChatFormatting.RED));
        }
      }
    }
  }

  public static class Properties extends Item.Properties {

    private ImmutableMultimap.Builder<Attribute, AttributeModifier> attributeModifiers =
        ImmutableMultimap.builder();
    private boolean fireImmunity;

    public Properties addAttributeModifier(Attribute attribute, AttributeModifier modifier) {
      this.attributeModifiers.put(attribute, modifier);
      return this;
    }

    public Properties setFireImmunity(boolean fireImmunity) {
      this.fireImmunity = fireImmunity;
      return this;
    }
  }
}
