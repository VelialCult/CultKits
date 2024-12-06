package ru.velialcult.kits.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import ru.velialcult.kits.manager.KitManager;
import ru.velialcult.kits.menu.KitsMenu;
import ru.velialcult.kits.menu.setting.KitListMenu;
import ru.velialcult.kits.model.Kit;
import ru.velialcult.kits.repository.KitRepository;
import ru.velialcult.kits.shop.menu.ShopMenu;
import ru.velialcult.library.bukkit.utils.PlayerUtil;
import ru.velialcult.library.core.VersionAdapter;
import ru.velialcult.library.java.text.ReplaceData;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class KitsCommand implements CommandExecutor, TabCompleter {

    private final Plugin plugin;
    private final FileConfiguration config;
    private final KitRepository kitRepository;
    private final KitManager kitManager;

    public KitsCommand(Plugin plugin,
                       FileConfiguration config,
                       KitRepository kitRepository,
                       KitManager kitManager) {
        this.plugin = plugin;
        this.config = config;
        this.kitRepository = kitRepository;
        this.kitManager = kitManager;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

            if (args.length == 0) {

                if (PlayerUtil.senderIsPlayer(commandSender)) {
                    Player player = (Player) commandSender;
                    KitsMenu.generateInventory(player);
                }

            } else {

                if (args[0].equalsIgnoreCase("help")) {

                    if (commandSender.hasPermission("cultkits.admin")) {
                        VersionAdapter.MessageUtils().sendMessage(commandSender, config.getStringList("messages.commands.kit.help"));
                        return true;
                    } else {
                        VersionAdapter.MessageUtils().sendMessage(commandSender, VersionAdapter.TextUtil().setReplaces(config.getString("messages.dont-have-permission"),
                                new ReplaceData("{value}", "cultkits.admin")));
                    }
                } else {

                    String cmd = args[0].toUpperCase();

                    switch (cmd) {

                        case "SHOP": {
                            if (commandSender instanceof Player player) {
                                ShopMenu.generateInventory(player);
                                break;
                            }
                        }

                        case "SETTINGS": {
                            if (commandSender instanceof Player player) {
                                if (commandSender.hasPermission("cultkits.admin")) {
                                    KitListMenu.generateInventory(player);
                                    break;
                                }
                            }
                        }

                        case "CREATE": {

                            if (commandSender instanceof Player player) {
                                if (commandSender.hasPermission("cultkits.admin")) {
                                    if (args.length < 3) {
                                        VersionAdapter.MessageUtils().sendMessage(player, VersionAdapter.TextUtil().setReplaces(config.getString("messages.commands.kit.create.usage")));
                                        return true;
                                    }

                                    String kitName = args[1];

                                    if (kitRepository.existsKitWithKey(kitName)) {
                                        VersionAdapter.MessageUtils().sendMessage(player, VersionAdapter.TextUtil().setReplaces(config.getString("messages.commands.kit.create.already-exists"),
                                                new ReplaceData("{value}", kitName)));
                                        return true;
                                    }

                                    boolean canBuy = false;

                                    try {
                                        canBuy = Boolean.parseBoolean(args[2]);
                                    } catch (Exception e) {
                                        plugin.getLogger().warning("Произошла ошибка при работе метода Boolean.parseBoolean(args[3]): " + e.getMessage());
                                        VersionAdapter.MessageUtils().sendMessage(player, "&6ᴋɪᴛѕ &8| &fПроизошла ошибка при работе плагина, сообщите об этом администратору. Код ошибки: &c" + e.getMessage());
                                    }

                                    kitRepository.saveKit(kitName, canBuy);

                                    if (canBuy) {

                                        VersionAdapter.MessageUtils().sendMessage(player, VersionAdapter.TextUtil().setReplaces(config.getString("messages.commands.kit.purchased.create.create"),
                                                new ReplaceData("{value}", kitName)));
                                    } else {

                                        VersionAdapter.MessageUtils().sendMessage(player, VersionAdapter.TextUtil().setReplaces(config.getString("messages.commands.kit.create.create"),
                                                new ReplaceData("{value}", kitName)));
                                    }
                                } else {
                                    VersionAdapter.MessageUtils().sendMessage(commandSender, VersionAdapter.TextUtil().setReplaces(config.getString("messages.dont-have-permission"),
                                            new ReplaceData("{value}", "cultkits.admin")));
                                }
                            }
                            break;
                        }

                        case "GIVE": {

                            if (commandSender.hasPermission("cultkits.admin")) {
                                if (args.length < 3) {
                                    VersionAdapter.MessageUtils().sendMessage(commandSender, VersionAdapter.TextUtil().setReplaces(config.getString("messages.commands.kit.give.usage")));
                                    return true;
                                }

                                String kitName = args[1];

                                if (!kitRepository.existsKitWithKey(kitName)) {
                                    VersionAdapter.MessageUtils().sendMessage(commandSender, VersionAdapter.TextUtil().setReplaces(config.getString("messages.commands.kit.give.not-exists"),
                                            new ReplaceData("{value}", kitName)));
                                    return true;
                                }

                                Kit kit = kitRepository.getKit(kitName);

                                Player target = Bukkit.getPlayer(args[2]);

                                if (target == null) {
                                    VersionAdapter.MessageUtils().sendMessage(commandSender, VersionAdapter.TextUtil().setReplaces(config.getString("messages.commands.kit.give.player-is-null"),
                                            new ReplaceData("{value}", args[2])));
                                    return true;
                                }

                                kitManager.give(target, kit);

                                VersionAdapter.MessageUtils().sendMessage(target, VersionAdapter.TextUtil().setReplaces(config.getString("messages.commands.kit.give.info"),
                                        new ReplaceData("{kit}", kit.key())));

                                VersionAdapter.MessageUtils().sendMessage(commandSender, VersionAdapter.TextUtil().setReplaces(config.getString("messages.commands.kit.give.give"),
                                        new ReplaceData("{kit}", kit.key()),
                                        new ReplaceData("{player}", target.getName())));

                                break;
                            } else {
                                VersionAdapter.MessageUtils().sendMessage(commandSender, VersionAdapter.TextUtil().setReplaces(config.getString("messages.dont-have-permission"),
                                        new ReplaceData("{value}", "cultkits.admin")));
                            }
                        }

                        case "DELETE": {

                            if (commandSender instanceof Player player) {
                                if (commandSender.hasPermission("cultkits.admin")) {
                                    String kitName = args[1];

                                    if (args.length != 2) {
                                        VersionAdapter.MessageUtils().sendMessage(player, VersionAdapter.TextUtil().setReplaces(config.getString("messages.commands.kit.delete.usage"),
                                                new ReplaceData("{value}", kitName)));
                                        return true;
                                    }

                                    if (!kitRepository.existsKitWithKey(kitName)) {
                                        VersionAdapter.MessageUtils().sendMessage(player, VersionAdapter.TextUtil().setReplaces(config.getString("messages.commands.kit.delete.not-exists"),
                                                new ReplaceData("{value}", kitName)));
                                        return true;
                                    }

                                    kitRepository.deleteKit(kitName);
                                    VersionAdapter.MessageUtils().sendMessage(player, VersionAdapter.TextUtil().setReplaces(config.getString("messages.commands.kit.delete.delete"),
                                            new ReplaceData("{value}", kitName)));

                                } else {
                                    VersionAdapter.MessageUtils().sendMessage(commandSender, VersionAdapter.TextUtil().setReplaces(config.getString("messages.dont-have-permission"),
                                            new ReplaceData("{value}", "cultkits.admin")));
                                }
                            }
                            break;
                        }

                        case "ADD",
                             "FULL": {

                            if (commandSender instanceof Player player) {
                                if (commandSender.hasPermission("cultkits.admin")) {

                                    String type = args[0].toUpperCase();

                                    String kitName = args[1];

                                    if (args.length != 2) {
                                        VersionAdapter.MessageUtils().sendMessage(player, VersionAdapter.TextUtil().setReplaces(config.getString("messages.commands.kit.add.usage"),
                                                new ReplaceData("{value}", kitName)));
                                        return true;
                                    }

                                    if (!kitRepository.existsKitWithKey(kitName)) {
                                        VersionAdapter.MessageUtils().sendMessage(player, VersionAdapter.TextUtil().setReplaces(config.getString("messages.commands.kit.add.not-exists"),
                                                new ReplaceData("{value}", kitName)));
                                        return true;
                                    }

                                    Kit kit = kitRepository.getKit(kitName);

                                    if (type.equalsIgnoreCase("ADD")) {

                                        ItemStack itemStack = player.getInventory().getItemInMainHand();

                                        if (itemStack == null) {
                                            VersionAdapter.MessageUtils().sendMessage(player, config.getString("messages.commands.kit.add.item-not-found"));
                                            return true;
                                        }

                                        kit.items().put(UUID.randomUUID(), itemStack);
                                        VersionAdapter.MessageUtils().sendMessage(player, VersionAdapter.TextUtil().setReplaces(config.getString("messages.commands.kit.add.add"),
                                                new ReplaceData("{value}", itemStack.getType())));
                                    } else if (type.equalsIgnoreCase("FULL")) {

                                        for (ItemStack itemStack : Arrays.stream(player.getInventory().getContents())
                                                .filter(Objects::nonNull)
                                                .collect(Collectors.toSet())) {
                                            kit.items().put(UUID.randomUUID(), itemStack);
                                        }

                                        VersionAdapter.MessageUtils().sendMessage(player, VersionAdapter.TextUtil().setReplaces(config.getString("messages.commands.kit.add.full"),
                                                new ReplaceData("{value}", kitName)));
                                    } else {
                                        VersionAdapter.MessageUtils().sendMessage(player, VersionAdapter.TextUtil().setReplaces(config.getString("messages.commands.kit.add.usage"),
                                                new ReplaceData("{value}", kitName)));
                                    }
                                } else {
                                    VersionAdapter.MessageUtils().sendMessage(commandSender, VersionAdapter.TextUtil().setReplaces(config.getString("messages.dont-have-permission"),
                                            new ReplaceData("{value}", "cultkits.admin")));
                                }
                            }

                            break;
                        }
                    }
                }
            }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        if (commandSender.hasPermission("cultkits.admin")) {
            if (args.length == 1) {
                return List.of("help", "create", "delete", "add", "full", "give", "settings", "shop");
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("delete")) {
                    return kitRepository.getKits()
                            .stream()
                            .map(Kit::key)
                            .toList();
                } else if (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("full")) {
                    return kitRepository.getKits()
                            .stream()
                            .map(Kit::key)
                            .toList();
                } else if (args[0].equalsIgnoreCase("give")) {
                    return kitRepository.getKits()
                            .stream()
                            .map(Kit::key)
                            .toList();
                }
            } else if (args.length == 3) {
                if (args[0].equalsIgnoreCase("create")) {
                    return List.of("true", "false");
                }
            }
        } else if (args.length == 0) {
            return List.of("shop");
        }

        return List.of();
    }
}
