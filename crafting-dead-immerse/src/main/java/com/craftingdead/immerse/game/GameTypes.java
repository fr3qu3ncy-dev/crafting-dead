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

package com.craftingdead.immerse.game;

import com.craftingdead.immerse.CraftingDeadImmerse;
import com.craftingdead.immerse.game.network.NetworkProtocol;
import com.craftingdead.immerse.game.survival.SurvivalClient;
import com.craftingdead.immerse.game.survival.SurvivalServer;
import com.craftingdead.immerse.game.tdm.TdmClient;
import com.craftingdead.immerse.game.tdm.TdmNetworkProtocol;
import com.craftingdead.immerse.game.tdm.TdmServer;
import com.mojang.serialization.Codec;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

public class GameTypes {

  public static final DeferredRegister<GameType> gameTypes =
      DeferredRegister.create(GameType.class, CraftingDeadImmerse.ID);

  public static final Lazy<IForgeRegistry<GameType>> registry =
      Lazy.of(gameTypes.makeRegistry("game_type", RegistryBuilder::new));

  public static final RegistryObject<GameType> SURVIVAL = gameTypes.register("survival",
      () -> new GameType(Codec.unit(SurvivalServer::new), () -> SurvivalClient::new,
          NetworkProtocol.EMPTY));

  public static final RegistryObject<GameType> TEAM_DEATHMATCH = gameTypes.register("tdm",
      () -> new GameType(TdmServer.CODEC, () -> TdmClient::new, TdmNetworkProtocol.INSTANCE));
}
