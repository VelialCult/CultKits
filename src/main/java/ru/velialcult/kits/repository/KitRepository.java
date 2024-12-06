package ru.velialcult.kits.repository;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import ru.velialcult.kits.loader.KitLoader;
import ru.velialcult.kits.model.DefaultKit;
import ru.velialcult.kits.model.Kit;
import ru.velialcult.kits.model.PurchasedKit;
import ru.velialcult.kits.providers.economy.EconomyType;
import ru.velialcult.kits.saver.KitSaver;
import ru.velialcult.library.bukkit.file.FileRepository;
import ru.velialcult.library.bukkit.utils.ConfigurationUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class KitRepository {

    private final Plugin plugin;

    private final List<Kit> kits;

    private final KitSaver saver;

    private final FileConfiguration kitsConfig;

    public KitRepository(Plugin plugin) {

        this.plugin = plugin;

        this.kits = new ArrayList<>();

        this.kitsConfig = FileRepository.getByName(plugin, "kits.yml").getConfiguration();

        KitLoader loader = new KitLoader(kitsConfig);

        kits.addAll(loader.loadKits());

        saver = new KitSaver(plugin, kitsConfig);
    }

    public boolean existsKitWithKey(String key) {
        return kits.stream().anyMatch(kit -> kit.key().equals(key));
    }

    public Kit getKit(String key) {
        return kits.stream().filter(kit -> kit.key().equals(key)).findFirst().orElse(null);
    }

    public List<Kit> getKits() {
        return kits;
    }

    public void saveKit(PurchasedKit purchasedKit) {

        saver.saveKit(purchasedKit);
    }

    public void saveKit(DefaultKit defaultKit) {

        saver.saveKit(defaultKit);
    }

    public void saveKit(String kitName,boolean canBuy) {

        Kit kit;

        if (canBuy) {

            kit = new PurchasedKit(kitName, new HashMap<>(), EconomyType.VAULT, 100);

        } else {

            kit = new DefaultKit(kitName, new HashMap<>(), "default", 0);

        }

        kits.add(kit);

        saver.saveKit(kit);
    }

    public void deleteKit(String kitName) {

        if (existsKitWithKey(kitName)) {

            Kit kit = getKit(kitName);

            kits.remove(kit);

            kitsConfig.set("kits." + kitName, null);

            ConfigurationUtil.saveFile(kitsConfig, plugin.getDataFolder().getAbsolutePath(), "kits.yml");
        }
    }

    public void saveKits() {
        saver.saveKits(kits);
    }
}
