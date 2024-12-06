package ru.velialcult.kits.manager;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import ru.velialcult.kits.dao.UserDao;
import ru.velialcult.kits.entity.KitDataEntity;
import ru.velialcult.kits.entity.UserEntity;
import ru.velialcult.kits.model.DefaultKit;
import ru.velialcult.kits.model.Kit;
import ru.velialcult.kits.model.PurchasedKit;
import ru.velialcult.kits.repository.KitRepository;
import ru.velialcult.library.bukkit.utils.PlayerUtil;
import ru.velialcult.library.core.VersionAdapter;
import ru.velialcult.library.java.utils.TimeUtil;

import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;

public class KitManager {

    private final Plugin plugin;
    private final PermissionManager permissionManager;
    private final KitRepository kitRepository;
    private final UserDao userDao;

    public KitManager(Plugin plugin,
                      PermissionManager permissionManager,
                      KitRepository kitRepository,
                      UserDao userDao) {
        this.plugin = plugin;
        this.permissionManager = permissionManager;
        this.kitRepository = kitRepository;
        this.userDao = userDao;
    }

    public void give(Player player, Kit kit) {
        List<ItemStack> items = kit.items()
                .values()
                .stream()
                .toList();

        for (ItemStack item : items) {
            PlayerUtil.giveItem(player, item);
        }
    }

    public void giveKit(Player player, Kit kit) {

        if (kit instanceof  DefaultKit defaultKit) {

            if (isAvailableForReceiving(player, defaultKit.key())) {

                List<ItemStack> items = defaultKit.items()
                        .values()
                        .stream()
                        .toList();

                for (ItemStack item : items) {
                    PlayerUtil.giveItem(player, item);
                }

                try {

                    userDao.takeKit(defaultKit.key(), player.getUniqueId());

                } catch (Exception exception) {
                    plugin.getLogger().warning("Произошла ошибка при работе метода void giveKit(Player player, Kit kit): " + exception.getMessage());
                    VersionAdapter.MessageUtils().sendMessage(player, "&6ᴋɪᴛѕ &8| &fПроизошла ошибка при работе плагина, сообщите об этом администратору. Код ошибки: &c" + exception.getMessage());
                }
            }
        } else if (kit instanceof PurchasedKit purchasedKit) {

            List<ItemStack> items = purchasedKit.items()
                    .values()
                    .stream()
                    .toList();

            for (ItemStack item : items) {
                PlayerUtil.giveItem(player, item);
            }
        }
    }

    public long getCooldownInSeconds(Player player, String kitName) {
        try {

            UserEntity user = userDao.queryById(player.getUniqueId());

            KitDataEntity kitDataEntity = userDao.getKitDataDao().queryIdOrCreate(kitName, user);

            DefaultKit defaultKit = (DefaultKit) kitRepository.getKit(kitName);

            Date date = kitDataEntity.getDate();

            Instant instant = Instant.ofEpochMilli(date.getTime()).plusSeconds(defaultKit.getCooldownInSeconds());

            return Duration.between(Instant.now(), instant).getSeconds();

        } catch (SQLException exception) {
            plugin.getLogger().warning("Произошла ошибка при работе метода long getCooldownInSeconds(Player player, String kitName): " + exception.getMessage());
            VersionAdapter.MessageUtils().sendMessage(player, "&6ᴋɪᴛѕ &8| &fПроизошла ошибка при работе плагина, сообщите об этом администратору. Код ошибки: &c" + exception.getErrorCode());
            return 0;
        }
    }

    public boolean isAvailableForReceiving(Player player, String kitName) {

        try {

            UserEntity user = userDao.queryById(player.getUniqueId());

            KitDataEntity kitDataEntity = userDao.getKitDataDao().queryIdOrCreate(kitName, user);

            Kit kit  = kitRepository.getKit(kitName);

            if (kit instanceof DefaultKit defaultKit) {

                Date date = kitDataEntity.getDate();

                Instant instant = Instant.ofEpochMilli(date.getTime()).plusSeconds(defaultKit.getCooldownInSeconds());

                return TimeUtil.getCurrentDate().toInstant().isAfter(instant);

            }

            return false;

        } catch (SQLException exception) {
            plugin.getLogger().warning("Произошла ошибка при работе метода boolean isAvailableForReceiving(Player player, String kitName): " + exception.getMessage());
            VersionAdapter.MessageUtils().sendMessage(player, "&6ᴋɪᴛѕ &8| &fПроизошла ошибка при работе плагина, сообщите об этом администратору. Код ошибки: &c" + exception.getErrorCode());
            return false;
        }
    }

    public DefaultKit getKitByGroup(Player player) {

        String group = permissionManager.getPrimaryGroup(player);

        return kitRepository.getKits().stream()
                .filter(kit -> kit instanceof DefaultKit)
                .map(kit -> (DefaultKit) kit)
                .filter(kit -> kit.getGroupName().equalsIgnoreCase(group))
                .findFirst()
                .orElse(null);
    }
}
