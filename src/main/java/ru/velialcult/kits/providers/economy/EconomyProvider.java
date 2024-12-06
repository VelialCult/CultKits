package ru.velialcult.kits.providers.economy;

import org.bukkit.entity.Player;

public interface EconomyProvider {

    void take(Player player, int amount);

    void give(Player player, int amount);

    int get(Player player);

    boolean has(Player player, int amount);

    EconomyType getType();
}
