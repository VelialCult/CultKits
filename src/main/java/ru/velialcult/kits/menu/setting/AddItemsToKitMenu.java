package ru.velialcult.kits.menu.setting;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.velialcult.kits.model.DefaultKit;
import ru.velialcult.kits.model.Kit;
import ru.velialcult.kits.model.PurchasedKit;
import ru.velialcult.library.core.VersionAdapter;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.inventory.VirtualInventory;
import xyz.xenondevs.invui.inventory.VirtualInventoryManager;
import xyz.xenondevs.invui.inventory.event.UpdateReason;
import xyz.xenondevs.invui.item.impl.SuppliedItem;
import xyz.xenondevs.invui.window.Window;

import java.util.*;
import java.util.stream.Collectors;

/**
 * ✨ Этот код был написан AveNilsson
 * 📅 Дата создания: 19.10.2024
 * ⏰ Время создания: 5:45
 */

public class AddItemsToKitMenu {

    protected static void generateInventory(Player player, Kit kit) {
        VirtualInventory virtualInventory = VirtualInventoryManager.getInstance().getOrCreate(player.getUniqueId(), 54);
        SuppliedItem closeButton = new SuppliedItem(() -> (s) ->
                VersionAdapter.getItemBuilder()
                        .setDisplayName("&cЗакрыть меню")
                        .setLore(new ArrayList<>())
                        .setType(Material.BARRIER)
                        .build(), (click) -> {
            virtualInventory.removeIf(UpdateReason.SUPPRESSED, Objects::nonNull);
            if (kit instanceof PurchasedKit purchasedKit) {
                PurchasedKitSettingMenu.generateInventory(player, purchasedKit);
            } else if (kit instanceof DefaultKit defaultKit) {
                DefaultKitSettingMenu.generateInventory(player, defaultKit);
            }
            return true;
        });

        for (ItemStack itemStack : kit.items().values()) {
            virtualInventory.addItem(UpdateReason.SUPPRESSED, itemStack);
        }

        SuppliedItem saveButton = new SuppliedItem(() -> (s) -> VersionAdapter.getItemBuilder()
                .setDisplayName("&aСохранить предметы")
                .setLore(new ArrayList(Arrays.asList(
                        "",
                        "§7Нажмите, чтобы сохранить все добавленые",
                        "§7предметы.",
                        "",
                        " &8- §eСохранить")))
                .setType(XMaterial.LIME_STAINED_GLASS_PANE.parseMaterial()).build(), (click) -> {
            List<ItemStack> nonNullItems = Arrays.stream(virtualInventory.getItems()).filter(Objects::nonNull).collect(Collectors.toList());
            kit.items().clear();
            nonNullItems.forEach(itemStack -> {
                UUID uuid = UUID.randomUUID();
                kit.items().put(uuid, itemStack);
            });
            virtualInventory.removeIf(UpdateReason.SUPPRESSED, Objects::nonNull);
            if (kit instanceof PurchasedKit purchasedKit) {
                PurchasedKitSettingMenu.generateInventory(player, purchasedKit);
            } else if (kit instanceof DefaultKit defaultKit) {
                DefaultKitSettingMenu.generateInventory(player, defaultKit);
            }
            return true;
        });

        Gui gui = Gui.normal().setStructure(new String[]{
                        "b b b b b b b b b",
                        "b v v v v v v v b",
                        "b v v v v v v v b",
                        "b v v v v v v v b",
                        "b b b b b b b b b",
                        ". . s . c . . . . "})
                .addIngredient('b', XMaterial.GRAY_STAINED_GLASS_PANE.parseItem())
                .addIngredient('v', virtualInventory)
                .addIngredient('s', saveButton)
                .addIngredient('c', closeButton)
                .build();
        Window window = Window.single().setViewer(player).setTitle("§8CultKits | Настройка предметов " + kit.key()).setGui(gui).build();
        window.open();
    }
}
