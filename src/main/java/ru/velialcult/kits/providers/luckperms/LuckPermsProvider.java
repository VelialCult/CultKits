package ru.velialcult.kits.providers.luckperms;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.PrefixNode;
import net.luckperms.api.node.types.SuffixNode;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * Class for managing LuckPerms services.
 */
public class LuckPermsProvider {

    private LuckPerms api;

    /**
     * Constructor for LuckPermsProvider.
     */
    public LuckPermsProvider() {
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            api = provider.getProvider();
        }
    }

    /**
     * Gets the group for a player.
     *
     * @param playerName String
     * @return String
     */
    public String getGroup(String playerName) {
        User user = api.getUserManager().getUser(playerName);

        if (user != null) {
            return user.getPrimaryGroup();
        }

        return "default";
    }

    /**
     * Gets the group prefix for a player.
     *
     * @param player Player instance
     * @return String
     */
    public String getGroupPrefix(Player player) {
        User user = api.getUserManager().getUser(player.getUniqueId());

        if (user != null) {
            String groupName = user.getPrimaryGroup();
            Group group = api.getGroupManager().getGroup(groupName);

            if (group != null) {
                return group.getCachedData().getMetaData().getPrefix();
            }
        }

        return null;
    }

    /**
     * Gets the suffix for a player.
     *
     * @param player Player instance
     * @return String
     */
    public String getSuffix(Player player) {
        if (api != null) {
            User user = api.getPlayerAdapter(Player.class).getUser(player);
            return user.getCachedData().getMetaData().getSuffix();
        }
        return "";
    }

    /**
     * Gets the prefix for a player.
     *
     * @param player Player instance
     * @return String
     */
    public String getPrefix(Player player) {
        if (api != null) {
            User user = api.getPlayerAdapter(Player.class).getUser(player);
            return user.getCachedData().getMetaData().getPrefix();
        }
        return "";
    }

    /**
     * Sets the prefix for a player.
     *
     * @param player Player instance
     * @param prefix String
     */
    public void setPrefix(Player player, String prefix) {
        if (api != null) {
            User user = api.getPlayerAdapter(Player.class).getUser(player);
            user.data().clear(node -> node instanceof PrefixNode);
            PrefixNode node = PrefixNode.builder(prefix, 1337).build();
            user.data().add(node);
            api.getUserManager().saveUser(user);
        }
    }

    /**
     * Sets the suffix for a player.
     *
     * @param player Player instance
     * @param suffix String
     */
    public void setSuffix(Player player, String suffix) {
        if (api != null) {
            User user = api.getPlayerAdapter(Player.class).getUser(player);
            user.data().clear(node -> node instanceof SuffixNode);
            SuffixNode node = SuffixNode.builder(suffix, 1337).build();
            user.data().add(node);
            api.getUserManager().saveUser(user);
        }
    }

    public String getGroupPrefix(String groupName) {
        Group group = api.getGroupManager().getGroup(groupName);

        if (group != null) {
            return group.getCachedData().getMetaData().getPrefix();
        }

        return "N/A";
    }

    public boolean groupExists(String groupName) {
        Group group = api.getGroupManager().getGroup(groupName);

        return group != null;
    }
}