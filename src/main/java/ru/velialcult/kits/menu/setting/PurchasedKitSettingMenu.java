package ru.velialcult.kits.menu.setting;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import ru.velialcult.kits.CultKits;
import ru.velialcult.kits.menu.KitsMenu;
import ru.velialcult.kits.model.PurchasedKit;
import ru.velialcult.kits.providers.economy.EconomyType;
import ru.velialcult.kits.repository.KitRepository;
import ru.velialcult.library.bukkit.inventory.PlayerInputHandler;
import ru.velialcult.library.bukkit.utils.InventoryUtil;
import ru.velialcult.library.core.VersionAdapter;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.impl.AutoUpdateItem;
import xyz.xenondevs.invui.item.impl.SuppliedItem;
import xyz.xenondevs.invui.window.Window;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * ✨ Этот код был написан AveNilsson
 * 📅 Дата создания: 14.10.2024
 * ⏰ Время создания: 18:54
 */

public class PurchasedKitSettingMenu {

    protected static void generateInventory(Player player, PurchasedKit purchasedKit) {

        KitRepository kitRepository = CultKits.getInstance().getKitRepository();

        SuppliedItem close = new SuppliedItem(() ->
                (s) -> VersionAdapter.getItemBuilder().setType(Material.BARRIER)
                        .setDisplayName("&cЗакрыть")
                        .build(), (click) -> {
            KitListMenu.generateInventory(player);
            return true;
        });

        SuppliedItem economyButton = InventoryUtil.createChangeModuleItem(purchasedKit,
                PurchasedKit::getEconomyType,
                PurchasedKit::setEconomyType,
                "&aУстановить тип валюты",
                new ArrayList<>(List.of(
                        "",
                        "&7Используйте данную кнопку для выбора",
                        "&7типа экономики, за которую будет покупаться",
                        "&7данный набор",
                        "",
                        "{type}",
                        "",
                        " &8- &eНажми, чтобы изменить")),
                EconomyType.class);

        AutoUpdateItem priceButton = new AutoUpdateItem(20, () ->
                (s) -> VersionAdapter.getItemBuilder().setType(XMaterial.SUNFLOWER.parseMaterial())
                        .setDisplayName("&aУстановить цену")
                        .setLore(new ArrayList<>(List.of(
                                "",
                                "&7Используйте данную кнопку для установки",
                                "&7стоимости покупки данной предмета",
                                "",
                                " &8- &7В данный момент набор будет",
                                "     &7стоить " + purchasedKit.getPrice() + " валюты " + purchasedKit.getEconomyType().name(),
                                "",
                                " &8- &eИзмениить"
                        )))
                        .build()) {
            public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent inventoryClickEvent) {
                VersionAdapter.MessageUtils().sendMessage(player,"&6ᴋɪᴛѕ &8| &fУкажите новую цену набора в &6чате");
                player.closeInventory();
                PlayerInputHandler.addPlayer(player, (str) -> {
                    int price = 0;

                    try {
                        price = Integer.parseInt(str);
                        purchasedKit.setPrice(price);
                        generateInventory(player, purchasedKit);
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
            AddItemsToKitMenu.generateInventory(player, purchasedKit);
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
            kitRepository.saveKit(purchasedKit);
            KitListMenu.generateInventory(player);
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
                .addIngredient('q', economyButton)
                .addIngredient('w', priceButton)
                .addIngredient('e', addItems)
                .addIngredient('c', close)
                .addIngredient('s', saveButton)
                .addIngredient('.', XMaterial.GRAY_STAINED_GLASS_PANE.parseItem());

        Window window = Window.single()
                .setViewer(player).setTitle("§8CultKits | Настройка платного набора " + purchasedKit.key())
                .setGui(builder.build())
                .build();
        window.open();
    }
}
