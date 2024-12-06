package ru.velialcult.kits.model;

import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;

public class DefaultKit implements Kit {

    private final String key;

    private final Map<UUID, ItemStack> items;

    private String groupName;

    private long cooldownInSeconds;

    public DefaultKit(String key, Map<UUID, ItemStack> items, String groupName, long cooldownInSeconds) {
        this.key = key;
        this.items = items;
        this.groupName = groupName;
        this.cooldownInSeconds = cooldownInSeconds;
    }

    public long getCooldownInSeconds() {
        return cooldownInSeconds;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setCooldownInSeconds(long cooldownInSeconds) {
        this.cooldownInSeconds = cooldownInSeconds;
    }

    @Override
    public String key() {
        return key;
    }

    @Override
    public Map<UUID, ItemStack> items() {
        return items;
    }
}
