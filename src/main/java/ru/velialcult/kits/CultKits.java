package ru.velialcult.kits;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import ru.velialcult.kits.command.KitsCommand;
import ru.velialcult.kits.dao.UserDao;
import ru.velialcult.kits.manager.KitManager;
import ru.velialcult.kits.manager.PermissionManager;
import ru.velialcult.kits.providers.ProvidersManager;
import ru.velialcult.kits.repository.KitRepository;
import ru.velialcult.kits.shop.ShopService;
import ru.velialcult.library.bukkit.file.FileRepository;
import ru.velialcult.library.bukkit.utils.ConfigurationUtil;
import ru.velialcult.library.core.VersionAdapter;

public class CultKits extends JavaPlugin {

    private static CultKits instance;

    private KitRepository kitRepository;

    private KitManager kitManager;

    private ConnectionSource connectionSource;

    private ProvidersManager providersManager;

    private PermissionManager permissionManager;

    private ShopService shopService;

    @Override

    public void onEnable() {

        instance = this;

        long mills = System.currentTimeMillis();

        try {

            this.saveDefaultConfig();

            ConfigurationUtil.loadConfiguration(this, "kits.yml");
            FileRepository.load(this);

            getLogger().info(" ");
            getLogger().info(VersionAdapter.TextUtil().colorize("  &fИдёт загрузка плагина &#FFA500CultKits " + getDescription().getVersion()));
            getLogger().info(VersionAdapter.TextUtil().colorize("  &fСпасибо вам от &#FFD700VelialCult&r&f, что выбрали наш плагин!"));
            getLogger().info(VersionAdapter.TextUtil().colorize("  &fВсю полезную информацию Вы сможете найти в нашем Discord сервере"));
            getLogger().info(VersionAdapter.TextUtil().colorize("  &fСсылка на Discord сервер: &#FFA500https://dsc.gg/velialcult"));
            getLogger().info(" ");

            providersManager = new ProvidersManager(this);

            providersManager.load();

            permissionManager = new PermissionManager(providersManager);

            kitRepository = new KitRepository(this);

            connectionSource = new JdbcConnectionSource(getConfig().getString("database.url"));

            UserDao userDao = new UserDao(connectionSource);

            kitManager = new KitManager(this, permissionManager, kitRepository, userDao);

            shopService = new ShopService(getConfig());

            KitsCommand kitsCommand = new KitsCommand(this, getConfig(), kitRepository, kitManager);

            Bukkit.getPluginCommand("kits").setExecutor(kitsCommand);
            Bukkit.getPluginCommand("kits").setTabCompleter(kitsCommand);

            getLogger().info(VersionAdapter.TextUtil().colorize("  &#00FF7F&lОтлично! &fПлагин был загружен за &#FFA500" + (System.currentTimeMillis() - mills)  + "ms"));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDisable() {
        if (kitRepository != null) {
            kitRepository.saveKits();
        }

        if (connectionSource != null && connectionSource.isOpen(getConfig().getString("database.url"))) {
            try {
                connectionSource.close();
            } catch (Exception exception) {
                getLogger().warning("Произошла ошибка при закрытии соединения с базой данных: " + exception.getMessage());
            }
        }
    }

    public static CultKits getInstance() {
        return instance;
    }

    public KitRepository getKitRepository() {
        return kitRepository;
    }

    public KitManager getKitManager() {
        return kitManager;
    }

    public ProvidersManager getProvidersManager() {
        return providersManager;
    }

    public PermissionManager getPermissionManager() {
        return permissionManager;
    }

    public ShopService getShopService() {
        return shopService;
    }
}
