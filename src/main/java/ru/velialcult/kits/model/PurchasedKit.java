package ru.velialcult.kits.model;

import org.bukkit.inventory.ItemStack;
import ru.velialcult.kits.providers.economy.EconomyType;

import java.util.Map;
import java.util.UUID;

/**
 * ✨ Этот код был написан AveNilsson
 * 📅 Дата создания: 14.10.2024
 * ⏰ Время создания: 17:31
 */

public class PurchasedKit implements Kit {

    private final String key;
    private final Map<UUID, ItemStack> items;

    private EconomyType economyType;
    private int price;

    public PurchasedKit(String key, Map<UUID, ItemStack> items, EconomyType economyType, int price) {
        this.key = key;
        this.items = items;
        this.economyType = economyType;
        this.price = price;
    }

    public EconomyType getEconomyType() {
        return economyType;
    }

    public int getPrice() {
        return price;
    }

    @Override
    public String key() {
        return key;
    }

    @Override
    public Map<UUID, ItemStack> items() {
        return items;
    }

    public void setEconomyType(EconomyType economyType) {
        this.economyType = economyType;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
