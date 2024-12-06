package ru.velialcult.kits.loader;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import ru.velialcult.kits.model.DefaultKit;
import ru.velialcult.kits.model.Kit;
import ru.velialcult.kits.model.PurchasedKit;
import ru.velialcult.kits.providers.economy.EconomyType;
import ru.velialcult.library.java.utils.TimeUtil;

import java.util.*;

public class KitLoader {

    private final FileConfiguration config;

    public KitLoader(FileConfiguration config) {
        this.config = config;
    }

    public List<Kit> loadKits() {

        List<Kit> defaultKits = new ArrayList<>();

        for (String kit : config.getConfigurationSection("kits").getKeys(false)) {

            String type = config.getString("kits." + kit + ".type");

            Map<UUID, ItemStack> items = new HashMap<>();

            if (config.contains("kits." + kit + ".items")) {

                for (String itemKey : config.getConfigurationSection("kits." + kit + ".items").getKeys(false)) {

                    UUID uuid = UUID.fromString(itemKey);

                    ItemStack itemStack = ItemStack.deserialize(config.getConfigurationSection("kits." + kit + ".items." + itemKey + ".data").getValues(true));

                    items.put(uuid, itemStack);
                }
            }

            if (type.equalsIgnoreCase("DEFAULT")) {

                String groupName = config.getString("kits." + kit + ".groupName");

                long cooldownInSeconds = TimeUtil.parseStringToTime(config.getString("kits." + kit + ".cooldown"));

                defaultKits.add(new DefaultKit(kit, items, groupName, cooldownInSeconds));

            } else if (type.equalsIgnoreCase("PURCHASED")) {

                EconomyType economyType = EconomyType.valueOf(config.getString("kits." + kit + ".economyType").toUpperCase());

                int price = config.getInt("kits." + kit + ".price");

                defaultKits.add(new PurchasedKit(kit, items, economyType, price));
            }
        }

        return defaultKits;
    }
}
