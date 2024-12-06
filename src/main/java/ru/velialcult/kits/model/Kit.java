package ru.velialcult.kits.model;

import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;

/**
 * ✨ Этот код был написан AveNilsson
 * 📅 Дата создания: 14.10.2024
 * ⏰ Время создания: 17:31
 */

public interface Kit {

    String key();

    Map<UUID, ItemStack> items();
}
