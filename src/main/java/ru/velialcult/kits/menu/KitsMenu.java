package ru.velialcult.kits.menu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ru.velialcult.kits.CultKits;
import ru.velialcult.kits.manager.KitManager;
import ru.velialcult.kits.manager.PermissionManager;
import ru.velialcult.kits.model.DefaultKit;
import ru.velialcult.library.bukkit.utils.InventoryUtil;
import ru.velialcult.library.core.VersionAdapter;
import ru.velialcult.library.java.text.ReplaceData;
import ru.velialcult.library.java.utils.TimeUtil;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.impl.AutoUpdateItem;
import xyz.xenondevs.invui.item.impl.SuppliedItem;
import xyz.xenondevs.invui.window.Window;

import java.util.List;
import java.util.Map;

public class KitsMenu {

    public static void generateInventory(Player player) {

        FileConfiguration config = CultKits.getInstance().getConfig();

        KitManager kitManager = CultKits.getInstance().getKitManager();

        DefaultKit primaryGroupDefaultKit = kitManager.getKitByGroup(player);

        PermissionManager permissionManager = CultKits.getInstance().getPermissionManager();

        String groupName = permissionManager.getPrimaryGroup(player);

        String[] structure =  config.getStringList("inventories.kit-menu.structure").toArray(new String[0]);

        char itemsChar = config.contains("inventories.kit-menu.items." + groupName + ".symbol") ? config.getString("inventories.kit-menu.items." + groupName + ".symbol").charAt(0) :  config.getString("inventories.kit-menu.items.not-found.symbol").charAt(0) ;

        Item item = new AutoUpdateItem(20, () ->
                s -> {
                    if (primaryGroupDefaultKit == null) {
                        return InventoryUtil.createItem(player, config, "inventories.kit-menu.items.not-found",
                                new ReplaceData("{group}", permissionManager.getGroupPrefix(player)));
                    } else {
                        ItemStack itemStack = InventoryUtil.createItem(player, config, "inventories.kit-menu.items." + groupName);
                        if (kitManager.isAvailableForReceiving(player, primaryGroupDefaultKit.key())) {
                            return VersionAdapter.getItemBuilder()
                                    .setItem(itemStack)
                                    .setDisplayName(VersionAdapter.TextUtil().colorize(VersionAdapter.TextUtil().setReplaces(
                                            config.getString("inventories.kit-menu.items.available.displayName"),
                                            new ReplaceData("{group}", permissionManager.getGroupPrefix(player)))))
                                    .setLore(VersionAdapter.TextUtil().colorize(VersionAdapter.TextUtil().setReplaces(
                                            config.getStringList("inventories.kit-menu.items.available.lore"),
                                            new ReplaceData("{cooldown}", TimeUtil.getTime(primaryGroupDefaultKit.getCooldownInSeconds())),
                                            new ReplaceData("{group}", permissionManager.getGroupPrefix(player)))))
                                    .build();
                        } else {
                            return VersionAdapter.getItemBuilder()
                                    .setType(Material.RED_STAINED_GLASS_PANE)
                                    .setDisplayName(VersionAdapter.TextUtil().colorize(VersionAdapter.TextUtil().setReplaces(
                                            config.getString("inventories.kit-menu.items.un-available.displayName"),
                                            new ReplaceData("{group}", permissionManager.getGroupPrefix(player)))))
                                    .setLore(VersionAdapter.TextUtil().colorize(VersionAdapter.TextUtil().setReplaces(
                                            config.getStringList("inventories.kit-menu.items.un-available.lore"),
                                            new ReplaceData("{cooldown}", TimeUtil.getTime(kitManager.getCooldownInSeconds(player, primaryGroupDefaultKit.key()))),
                                            new ReplaceData("{group}", permissionManager.getGroupPrefix(player)))))
                                    .build();
                        }
                    }
                }
        ) {
            @Override
            public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent inventoryClickEvent) {
                if (clickType == ClickType.LEFT) {
                    if (kitManager.isAvailableForReceiving(player, primaryGroupDefaultKit.key())) {
                        kitManager.giveKit(player, primaryGroupDefaultKit);
                        player.closeInventory();
                    }
                } else if (clickType == ClickType.RIGHT) {
                    PreviewMenu.generateInventory(primaryGroupDefaultKit, player);
                }
            }
        };

        Map<Character, SuppliedItem> customItemList = InventoryUtil.createItems(config,
                "inventories.kit-menu.items",
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

         Gui.Builder.Normal builder = Gui.normal()
                .setStructure(structure)
                .addIngredient(itemsChar, item);

        InventoryUtil.setItems(builder, customItemList);

        Window window = Window.single()
                .setViewer(player)
                .setTitle(VersionAdapter.TextUtil().colorize(config.getString("inventories.kit-menu.title")))
                .setGui(builder.build())
                .build();
        window.open();
    }
}
