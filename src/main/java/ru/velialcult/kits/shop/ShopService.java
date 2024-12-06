package ru.velialcult.kits.shop;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import ru.velialcult.kits.CultKits;
import ru.velialcult.kits.manager.KitManager;
import ru.velialcult.kits.model.PurchasedKit;
import ru.velialcult.kits.providers.economy.EconomyType;
import ru.velialcult.kits.providers.economy.playerpoints.PlayerPointsProvider;
import ru.velialcult.kits.providers.economy.vault.VaultProvider;
import ru.velialcult.library.core.VersionAdapter;
import ru.velialcult.library.java.text.ReplaceData;

/**
 * ✨ Этот код был написан AveNilsson
 * 📅 Дата создания: 19.10.2024
 * ⏰ Время создания: 6:01
 */

public class ShopService {

 private final FileConfiguration config;

 public ShopService(FileConfiguration config) {
  this.config = config;
 }

 public void buyKit(Player player, PurchasedKit purchasedKit) {

  KitManager kitManager = CultKits.getInstance().getKitManager();

  EconomyType economyType = purchasedKit.getEconomyType();

  boolean has = switch (economyType) {

   case VAULT -> {

    VaultProvider vaultProvider = CultKits.getInstance().getProvidersManager().getVaultProvider();

    yield vaultProvider.has(player, purchasedKit.getPrice());
   }
   case PLAYER_POINTS -> {

    PlayerPointsProvider playerPointsProvider = CultKits.getInstance().getProvidersManager().getPlayerPointsProvider();

    yield playerPointsProvider.has(player, purchasedKit.getPrice());
   }
  };

  if (!has) {
   VersionAdapter.MessageUtils().sendMessage(player, VersionAdapter.TextUtil().setReplaces(config.getString("messages.shop.dont-have-money"),
           new ReplaceData("{value}", purchasedKit.getPrice())));
   player.closeInventory();
  } else {

   kitManager.giveKit(player, purchasedKit);

   switch (economyType) {

    case VAULT -> {

     VaultProvider vaultProvider = CultKits.getInstance().getProvidersManager().getVaultProvider();

     vaultProvider.take(player, purchasedKit.getPrice());
    }
    case PLAYER_POINTS -> {

     PlayerPointsProvider playerPointsProvider = CultKits.getInstance().getProvidersManager().getPlayerPointsProvider();

     playerPointsProvider.take(player, purchasedKit.getPrice());
    }
   };

   VersionAdapter.MessageUtils().sendMessage(player, VersionAdapter.TextUtil().setReplaces(config.getString("messages.shop.buy"),
           new ReplaceData("{value}", purchasedKit.getPrice()),
           new ReplaceData("{kit}", purchasedKit.key())));
   player.closeInventory();
  }
 }
}
