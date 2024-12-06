package ru.velialcult.kits.menu;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import ru.velialcult.kits.CultKits;
import ru.velialcult.kits.menu.buttons.ScrollDownButton;
import ru.velialcult.kits.menu.buttons.ScrollUpButton;
import ru.velialcult.kits.model.DefaultKit;
import ru.velialcult.kits.model.Kit;
import ru.velialcult.kits.model.PurchasedKit;
import ru.velialcult.kits.shop.menu.ShopMenu;
import ru.velialcult.library.bukkit.utils.InventoryUtil;
import ru.velialcult.library.core.VersionAdapter;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.ScrollGui;
import xyz.xenondevs.invui.gui.structure.Markers;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.impl.SuppliedItem;
import xyz.xenondevs.invui.window.Window;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PreviewMenu {

    public static void generateInventory(Kit kit, Player player) {

        FileConfiguration config = CultKits.getInstance().getConfig();

        String[] structure =  config.getStringList("inventories.preview-menu.structure").toArray(new String[0]);

        List<Item> itemList = kit.items().values()
                .stream()
                .map(itemStack -> new SuppliedItem(() ->
                        (s) -> itemStack, (click) -> true)
                ).collect(Collectors.toList());

        Gui.Builder<ScrollGui<Item>, ScrollGui.Builder<Item>> builder = ScrollGui.items()
                .setStructure(structure)
                .addIngredient('#', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
                .addIngredient('u', new ScrollUpButton())
                .addIngredient('d', new ScrollDownButton())
                .setContent(itemList);

        Map<Character, SuppliedItem> customItemList = InventoryUtil.createItems(config,
                "inventories.preview-menu.items",
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

                        if (command.equals("[close]")) {
                            if (kit instanceof PurchasedKit) {
                                ShopMenu.generateInventory(player);
                            } else {
                                KitsMenu.generateInventory(player);
                            }
                        }
                    }
                });

        InventoryUtil.setItems(builder, customItemList);

        Window window = Window.single()
                .setViewer(player)
                .setTitle(VersionAdapter.TextUtil().colorize(config.getString("inventories.preview-menu.title")))
                .setGui(builder.build())
                .build();
        window.open();
    }
}
