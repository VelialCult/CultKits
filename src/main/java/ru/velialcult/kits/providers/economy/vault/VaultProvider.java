package ru.velialcult.kits.providers.economy.vault;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import ru.velialcult.kits.providers.economy.EconomyProvider;
import ru.velialcult.kits.providers.economy.EconomyType;

public class VaultProvider implements EconomyProvider {
    private Chat chat;
    private Economy economy;
    protected Permission permission;

    public Economy getEconomy() {
        if (economy == null) {
            setupEconomy();
        }

        return economy;
    }

    public Permission getPermission() {
        if (permission == null) {
            setupPermission();
        }

        return permission;
    }

    public void setupPermission() {
        RegisteredServiceProvider<Permission> permissionProvider = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }

    }

    public void setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

    }

    public boolean setupChat() {
        RegisteredServiceProvider<Chat> chatProvider = Bukkit.getServer().getServicesManager().getRegistration(Chat.class);
        if (chatProvider != null) {
            chat = chatProvider.getProvider();
        }

        return chat != null;
    }

    public Chat getChat() {
        if (chat == null) {
            setupChat();
        }

        return chat;
    }

    @Override
    public void take(Player player, int amount) {
        if (getEconomy().has(player, amount)) {
            getEconomy().withdrawPlayer(player, amount);
        }
    }

    @Override
    public void give(Player player, int amount) {
        getEconomy().depositPlayer(player, amount);
    }

    @Override
    public int get(Player player) {
        return (int) getEconomy().getBalance(player);
    }

    public boolean has(Player player, int amount) {
        return get(player) >= amount;
    }

    @Override
    public EconomyType getType() {
        return EconomyType.VAULT;
    }
}
