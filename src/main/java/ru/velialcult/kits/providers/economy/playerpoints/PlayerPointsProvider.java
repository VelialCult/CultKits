package ru.velialcult.kits.providers.economy.playerpoints;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.entity.Player;
import ru.velialcult.kits.providers.economy.EconomyProvider;
import ru.velialcult.kits.providers.economy.EconomyType;

public class PlayerPointsProvider implements EconomyProvider {

    private final PlayerPointsAPI playerPointsAPI;

    public PlayerPointsProvider() {
        this.playerPointsAPI = PlayerPoints.getInstance().getAPI();
    }

    public int get(Player player) {
        return playerPointsAPI.look(player.getUniqueId());
    }

    @Override
    public boolean has(Player player, int amount) {
        return get(player) >= amount;
    }

    @Override
    public EconomyType getType() {
        return EconomyType.PLAYER_POINTS;
    }

    public void take(Player player, int amount) {
        if (playerPointsAPI.look(player.getUniqueId()) >= amount) {
            playerPointsAPI.take(player.getUniqueId(), amount);
        }
    }

    public void give(Player player, int amount) {
        playerPointsAPI.give(player.getUniqueId(), amount);
    }
}
