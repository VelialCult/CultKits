package ru.velialcult.kits.manager;

import org.bukkit.entity.Player;
import ru.velialcult.kits.providers.ProvidersManager;
import ru.velialcult.kits.providers.luckperms.LuckPermsProvider;

public class PermissionManager {

    private final ProvidersManager providersManager;

    public PermissionManager(ProvidersManager providersManager) {
        this.providersManager = providersManager;
    }

    public String getPrimaryGroup(Player player) {
        if (providersManager.useLuckPerms()) {

            LuckPermsProvider luckPermsProvider = providersManager.getLuckPermsProvider();

            return luckPermsProvider.getGroup(player.getName());
        }

        return "default";
    }

    public String getGroupPrefix(Player player) {
        if (providersManager.useLuckPerms()) {

            LuckPermsProvider luckPermsProvider = providersManager.getLuckPermsProvider();

            return luckPermsProvider.getGroupPrefix(player);
        }

        return "N/A";
    }
}
