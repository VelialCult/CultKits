package ru.velialcult.kits.menu.buttons;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import ru.velialcult.kits.CultKits;
import ru.velialcult.library.core.VersionAdapter;
import xyz.xenondevs.invui.gui.ScrollGui;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.controlitem.ScrollItem;

import java.util.List;

public class ScrollDownButton extends ScrollItem {
    public ScrollDownButton() {
        super(1);
    }

    @Override
    public ItemProvider getItemProvider(ScrollGui<?> gui) {
        FileConfiguration fileConfiguration = CultKits.getInstance().getConfig();
        ItemStack itemStack;
        String materialName = fileConfiguration.getString("inventories.buttons.scroll-down-button.item.material");
        String url = fileConfiguration.getString("inventories.buttons.scroll-down-button.item.head");
        String displayName = VersionAdapter.TextUtil().colorize(VersionAdapter.TextUtil().setReplaces(fileConfiguration.getString("inventories.buttons.scroll-down-button.displayName")));
        List<String> lore = VersionAdapter.TextUtil().colorize(VersionAdapter.TextUtil().setReplaces(fileConfiguration.getStringList("inventories.buttons.scroll-down-button.lore")));

        if (materialName.equalsIgnoreCase("head")) {
            itemStack = VersionAdapter.getSkullBuilder()
                    .setDisplayName(displayName)
                    .setLore(lore)
                    .setTexture(url)
                    .build();

        } else {
            itemStack  = VersionAdapter.getItemBuilder()
                    .setType(materialName)
                    .setDisplayName(displayName)
                    .setLore(lore)
                    .build();
        }

        if (!gui.canScroll(-1))
            itemStack.getItemMeta().setDisplayName("§cВы достигли конца страницы");

        return new ItemBuilder(itemStack);
    }
}
