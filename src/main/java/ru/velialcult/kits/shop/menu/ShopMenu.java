package ru.velialcult.kits.shop.menu;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import ru.velialcult.kits.CultKits;
import ru.velialcult.kits.menu.PreviewMenu;
import ru.velialcult.kits.menu.buttons.BackButton;
import ru.velialcult.kits.menu.buttons.ForwardButton;
import ru.velialcult.kits.model.PurchasedKit;
import ru.velialcult.kits.repository.KitRepository;
import ru.velialcult.kits.shop.ShopService;
import ru.velialcult.library.bukkit.utils.InventoryUtil;
import ru.velialcult.library.core.VersionAdapter;
import ru.velialcult.library.java.text.ReplaceData;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.gui.structure.Markers;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.impl.SuppliedItem;
import xyz.xenondevs.invui.window.Window;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ✨ Этот код был написан AveNilsson
 * 📅 Дата создания: 19.10.2024
 * ⏰ Время создания: 10:02
 */

public class ShopMenu {

    public static void generateInventory(Player player) {

        FileConfiguration config = CultKits.getInstance().getConfig();

        KitRepository kitRepository = CultKits.getInstance().getKitRepository();

        ShopService shopService = CultKits.getInstance().getShopService();

        String[] structure =  config.getStringList("inventories.shop.structure").toArray(new String[0]);

        List<Item> items = kitRepository.getKits()
                .stream()
                .filter(kit -> kit instanceof PurchasedKit)
                .map(kit -> (PurchasedKit) kit)
                .map(purchasedKit -> new SuppliedItem(() ->
                        (s) -> InventoryUtil.createItem(config, "inventories.shop.items.default",
                                new ReplaceData("{kit}", purchasedKit.key()),
                                new ReplaceData("{price}", purchasedKit.getPrice()),
                                new ReplaceData("{economy}", purchasedKit.getEconomyType())),
                        (click) -> {
                    if (click.getClickType() == ClickType.LEFT) {
                        shopService.buyKit(player, purchasedKit);
                    } else if (click.getClickType() == ClickType.RIGHT) {
                        PreviewMenu.generateInventory(purchasedKit, player);
                    }
                    return true;
                        })).collect(Collectors.toList());

        Map<Character, SuppliedItem> customItemList = InventoryUtil.createItems(config,
                "inventories.shop.items",
                (click, path) -> {
                    List<String> commands = config.getStringList(path + ".actionOnClick");
                    for (String command : commands) {
                        if (command.startsWith("[message]")) {
                            String message = command.replace("[message]", "");
                            VersionAdapter.MessageUtils().sendMessage(player, message);
                        }

                        if (command.startsWith("[execute]")) {
                            String executeCommand = command.replace("[execute]", "");
                            Bukkit.dispatchCommand(player, executeCommand);
                        }
                    }
                });

        Gui.Builder<PagedGui<Item>, PagedGui.Builder<Item>> builder = PagedGui.items()
                .setStructure(structure)
                .setContent(items)
                .addIngredient('b', new BackButton())
                .addIngredient('n', new ForwardButton())
                .addIngredient('#', Markers.CONTENT_LIST_SLOT_HORIZONTAL);

        InventoryUtil.setItems(builder, customItemList);

        Window window = Window.single()
                .setViewer(player)
                .setTitle(VersionAdapter.TextUtil().colorize(config.getString("inventories.shop.title")))
                .setGui(builder.build())
                .build();
        window.open();
    }
}
