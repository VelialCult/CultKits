package ru.velialcult.kits.saver;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import ru.velialcult.kits.model.DefaultKit;
import ru.velialcult.kits.model.Kit;
import ru.velialcult.kits.model.PurchasedKit;
import ru.velialcult.library.bukkit.utils.ConfigurationUtil;
import ru.velialcult.library.java.utils.TimeUtil;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class KitSaver {

    private final Plugin plugin;
    private final FileConfiguration configuration;

    public KitSaver(Plugin plugin, FileConfiguration configuration) {
        this.plugin = plugin;
        this.configuration = configuration;
    }

    public void saveKit(Kit kit) {

        if (kit instanceof DefaultKit defaultKit) {

            configuration.set("kits." + kit.key() + ".type", "DEFAULT");

            configuration.set("kits." + kit.key() + ".groupName", defaultKit.getGroupName());

            configuration.set("kits." + kit.key() + ".cooldown", TimeUtil.parseTimeToString(defaultKit.getCooldownInSeconds()));

        } else if (kit instanceof PurchasedKit purchasedKit) {

            configuration.set("kits." + kit.key() + ".type", "PURCHASED");

            configuration.set("kits." + kit.key() + ".price", purchasedKit.getPrice());

            configuration.set("kits." + kit.key() + ".economyType", purchasedKit.getEconomyType().name());
        }

        configuration.set("kits." + kit.key() + ".items", null);

        for (Map.Entry<UUID, ItemStack> entry : kit.items().entrySet()) {

            configuration.set("kits." + kit.key() + ".items." + entry.getKey().toString() + ".data", entry.getValue().serialize());
        }

        ConfigurationUtil.saveFile(configuration, plugin.getDataFolder().getAbsolutePath(), "kits.yml");
    }

    public void saveKits(List<Kit> kits) {

        for (Kit kit : kits) {

            if (kit instanceof DefaultKit defaultKit) {

                configuration.set("kits." + kit.key() + ".type", "DEFAULT");

                configuration.set("kits." + kit.key() + ".groupName", defaultKit.getGroupName());

                configuration.set("kits." + kit.key() + ".cooldown", TimeUtil.parseTimeToString(defaultKit.getCooldownInSeconds()));

            } else if (kit instanceof PurchasedKit purchasedKit) {

                configuration.set("kits." + kit.key() + ".type", "PURCHASED");

                configuration.set("kits." + kit.key() + ".price", purchasedKit.getPrice());

                configuration.set("kits." + kit.key() + ".economyType", purchasedKit.getEconomyType().name());
            }

            configuration.set("kits." + kit.key() + ".items", null);

            for (Map.Entry<UUID, ItemStack> entry : kit.items().entrySet()) {

                configuration.set("kits." + kit.key() + ".items." + entry.getKey().toString() + ".data", entry.getValue().serialize());
            }
        }

        ConfigurationUtil.saveFile(configuration, plugin.getDataFolder().getAbsolutePath(), "kits.yml");
    }
}
