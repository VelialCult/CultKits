package ru.velialcult.kits.menu.setting;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import ru.velialcult.kits.CultKits;
import ru.velialcult.kits.model.DefaultKit;
import ru.velialcult.kits.providers.luckperms.LuckPermsProvider;
import ru.velialcult.kits.repository.KitRepository;
import ru.velialcult.kits.saver.KitSaver;
import ru.velialcult.library.bukkit.inventory.PlayerInputHandler;
import ru.velialcult.library.core.VersionAdapter;
import ru.velialcult.library.java.utils.TimeUtil;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.impl.AutoUpdateItem;
import xyz.xenondevs.invui.item.impl.SuppliedItem;
import xyz.xenondevs.invui.window.Window;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * ✨ Этот код был написан AveNilsson
 * 📅 Дата создания: 19.10.2024
 * ⏰ Время создания: 5:29
 */

public class DefaultKitSettingMenu {

 protected static void generateInventory(Player player, DefaultKit defaultKit) {

  KitRepository kitRepository = CultKits.getInstance().getKitRepository();

  SuppliedItem close = new SuppliedItem(() ->
          (s) -> VersionAdapter.getItemBuilder().setType(Material.BARRIER)
                  .setDisplayName("&cЗакрыть")
                  .build(), (click) -> {
   KitListMenu.generateInventory(player);
   return true;
  });

  AutoUpdateItem group = new AutoUpdateItem(20, () ->
          (s) -> {
            return VersionAdapter.getItemBuilder().setType(XMaterial.PAPER.parseMaterial())
                    .setDisplayName("&aПривилегия")
                    .setLore(new ArrayList<>(List.of(
                            "",
                            "&7Данная настройка позволит Вам установить",
                            "&7привилегию, для которой будет доступен",
                            "&7данный набор",
                            "",
                            " &8- &fТекущая привилегия: " + defaultKit.getGroupName(),
                            "",
                            " &8- &eНажми, чтобы изменить")))
                    .build();
          }) {
   public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent inventoryClickEvent) {
    VersionAdapter.MessageUtils().sendMessage(player,"&6ᴋɪᴛѕ &8| &fУкажите новую привилегию в &6чате");
    player.closeInventory();
    PlayerInputHandler.addPlayer(player, (str) -> {
     LuckPermsProvider luckPermsProvider = CultKits.getInstance().getProvidersManager().getLuckPermsProvider();
     if (!luckPermsProvider.groupExists(str)) {
      VersionAdapter.MessageUtils().sendMessage(player,"&6ᴋɪᴛѕ &8| &cПривилегия " + str + " &cне найдена");
      return;
     }

     defaultKit.setGroupName(str);
     generateInventory(player, defaultKit);
    });
   }
  };

  AutoUpdateItem cooldown = new AutoUpdateItem(20, () ->
          (s) -> VersionAdapter.getItemBuilder().setType(XMaterial.CLOCK.parseMaterial())
                  .setDisplayName("&aЗадержка получения")
                  .setLore(new ArrayList<>(List.of(
                          "",
                          "&7С помощью данной настройки Вы можете задать,",
                          "&7как часто игрок сможет получать данный набор",
                          "",
                          " &8- &fТекущая задержка: ",
                          "      &8- &a" + TimeUtil.getTime(defaultKit.getCooldownInSeconds()),
                          "",
                          " &8- &eНажми, чтобы изменить"
                  ))).build()) {
   public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent inventoryClickEvent) {
    VersionAdapter.MessageUtils().sendMessage(player, "&6ᴋɪᴛѕ &8| &fУкажите новое время в &6чате");
    player.closeInventory();
    PlayerInputHandler.addPlayer(player, (str) -> {
     long cooldown;

     try {
      cooldown = TimeUtil.parseStringToTime(str);
      defaultKit.setCooldownInSeconds(cooldown);
      generateInventory(player, defaultKit);
     } catch (Exception e) {
      VersionAdapter.MessageUtils().sendMessage(player,"&6ᴋɪᴛѕ &8| &cВы указали неверное значение: " + str);
     }
    });
   }
  };

  SuppliedItem addItems = new SuppliedItem( () ->
          (s) -> VersionAdapter.getItemBuilder().setType(XMaterial.DIAMOND_HELMET.parseMaterial())
                  .setDisplayName("&aИзменить предметы")
                  .setLore(new ArrayList<>(List.of(
                          "",
                          "&7Используйте данное меню для изменения",
                          "&7содержимого набора",
                          "",
                          " &8- &eИзменить"
                  ))).build(), (click) -> {
   AddItemsToKitMenu.generateInventory(player, defaultKit);
   return true;
  });

  SuppliedItem saveButton = new SuppliedItem(() -> (s) -> VersionAdapter.getItemBuilder()
          .setDisplayName("&aСохранить предметы")
          .setLore(new ArrayList(Arrays.asList(
                  "",
                  "§7Нажмите, чтобы сохранить все добавленные",
                  "§7предметы.",
                  "",
                  " &8- §eСохранить")))
          .setType(XMaterial.LIME_STAINED_GLASS_PANE.parseMaterial()).build(), (click) -> {
   KitListMenu.generateInventory(player);
   kitRepository.saveKit(defaultKit);
   return true;
  });

  Gui.Builder builder = Gui.normal()
          .setStructure(new String[]{
                  ". . . . . . . . .",
                  ". q # w # e # # .",
                  ". # # # # # # # .",
                  ". # # # s # # # .",
                  ". # # # # # # # .",
                  ". . . . c . . . ."
          })
          .addIngredient('q', group)
          .addIngredient('w', cooldown)
          .addIngredient('e', addItems)
          .addIngredient('c', close)
          .addIngredient('s', saveButton)
          .addIngredient('.', XMaterial.GRAY_STAINED_GLASS_PANE.parseItem());

  Window window = Window.single()
          .setViewer(player).setTitle("§8CultKits | Настройка набора " + defaultKit.key())
          .setGui(builder.build())
          .build();
  window.open();
 }
}

