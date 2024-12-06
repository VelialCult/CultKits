package ru.velialcult.kits.menu.setting;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import ru.velialcult.kits.CultKits;
import ru.velialcult.kits.menu.buttons.BackButton;
import ru.velialcult.kits.menu.buttons.ForwardButton;
import ru.velialcult.kits.menu.buttons.ScrollDownButton;
import ru.velialcult.kits.menu.buttons.ScrollUpButton;
import ru.velialcult.kits.model.DefaultKit;
import ru.velialcult.kits.model.PurchasedKit;
import ru.velialcult.kits.providers.luckperms.LuckPermsProvider;
import ru.velialcult.kits.repository.KitRepository;
import ru.velialcult.library.core.VersionAdapter;
import ru.velialcult.library.java.utils.TimeUtil;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.gui.structure.Markers;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.impl.AutoUpdateItem;
import xyz.xenondevs.invui.item.impl.SuppliedItem;
import xyz.xenondevs.invui.window.Window;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ✨ Этот код был написан AveNilsson
 * 📅 Дата создания: 14.10.2024
 * ⏰ Время создания: 17:44
 */

public class KitListMenu {

    public static void generateInventory(Player player) {

        KitRepository kitRepository = CultKits.getInstance().getKitRepository();

        LuckPermsProvider luckPermsProvider = CultKits.getInstance().getProvidersManager().getLuckPermsProvider();

        List<Item> items = kitRepository.getKits().stream()
                .map( kit -> new AutoUpdateItem(20, () ->
                        (s) -> {

                            if (kit instanceof DefaultKit defaultKit) {

                                return VersionAdapter.getSkullBuilder()
                                        .setDisplayName("&fОбычный набор: &a" + defaultKit.key())
                                        .setLore(new ArrayList<>(List.of(
                                                "",
                                                "&7Обычные наборы доступны для получения",
                                                "&7игркам с указанной привилегией.",
                                                "",
                                                " &8- &fДоступен для группы: " + luckPermsProvider.getGroupPrefix(defaultKit.getGroupName()),
                                                " &8- &fЗадержка получения: &a" + TimeUtil.getTime(defaultKit.getCooldownInSeconds()),
                                                "",
                                                " &8- &eНажмите для настройки"
                                        )))
                                        .setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjBiMDY4NzA5NzkwZDQxYjg5MjdiODQyMmQyMWJiNTI0MDRiNTViNGNhMzUyY2RiN2M2OGU0YjM2NTkyNzIxIn19fQ==")
                                        .build();
                            }  else {
                                PurchasedKit purchasedKit = (PurchasedKit) kit;
                                return VersionAdapter.getSkullBuilder()
                                        .setDisplayName("&fПлатный набор: &a" + purchasedKit.key())
                                        .setLore(new ArrayList<>(List.of(
                                                "",
                                                "&7Обычные наборы доступны для покупки",
                                                "&7игркам за указанную валюту и не имеют",
                                                "&7задержки между получениями.",
                                                "",
                                                " &8- &fВалюта: &a" + purchasedKit.getEconomyType().name(),
                                                " &8- &fСтоимость: &e" + purchasedKit.getPrice(),
                                                "",
                                                " &8- &eНажмите для настройки"
                                        )))
                                        .setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWM5NmJlNzg4NmViN2RmNzU1MjVhMzYzZTVmNTQ5NjI2YzIxMzg4ZjBmZGE5ODhhNmU4YmY0ODdhNTMifX19")
                                        .build();
                            }
                        }) {
                    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent inventoryClickEvent) {
                        if (kit instanceof DefaultKit defaultKit) {
                            DefaultKitSettingMenu.generateInventory(player, defaultKit);
                        } else if (kit instanceof PurchasedKit purchasedKit){
                            PurchasedKitSettingMenu.generateInventory(player, purchasedKit);
                        }
                    }
                }).collect(Collectors.toList());

        SuppliedItem close = new SuppliedItem(() ->
                (s) -> VersionAdapter.getItemBuilder().setType(Material.BARRIER)
                        .setDisplayName("&cЗакрыть")
                        .build(), (click) -> {
            player.closeInventory();
            return true;
        });

        Gui.Builder<PagedGui<Item>, PagedGui.Builder<Item>> builder = PagedGui.items()
                .setStructure(new String[]{
                        ". . . . . . . . .",
                        ". # # # # # # # .",
                        ". # # # # # # # .",
                        ". # # # # # # # .",
                        ". # # # # # # # .",
                        ". . b . c . n . ."
                })
                .addIngredient('#', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
                .addIngredient('n', new ForwardButton())
                .addIngredient('b', new BackButton())
                .addIngredient('c', close)
                .setContent(items);

        Window window = Window.single()
                .setViewer(player).setTitle("§8CultKits | Настройка наборов")
                .setGui(builder.build())
                .build();
        window.open();
    }
}
