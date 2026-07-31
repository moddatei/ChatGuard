package net.chatguard.commands;

import net.chatguard.ChatGuardPlugin;
import net.chatguard.config.ConfigManager;
import net.chatguard.managers.ChatMuteManager;
import net.chatguard.managers.FilterManager;
import net.chatguard.utils.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Handles execution and tab-completion for /chatguard and /cg commands.
 */
public class ChatGuardCommand implements CommandExecutor, TabCompleter {

    private static final String PERM_ADMIN = "chatguard.admin";
    private static final String PERM_CLEAR = "chatguard.clear";
    private static final String PERM_MUTECHAT = "chatguard.mutechat";

    private final ChatGuardPlugin plugin;
    private final ConfigManager configManager;
    private final FilterManager filterManager;
    private final ChatMuteManager muteManager;

    public ChatGuardCommand(ChatGuardPlugin plugin, ConfigManager configManager,
                            FilterManager filterManager, ChatMuteManager muteManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.filterManager = filterManager;
        this.muteManager = muteManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();
        switch (subCommand) {
            case "clear":
                handleClear(sender);
                break;
            case "mutechat":
            case "mute":
                handleMuteChat(sender);
                break;
            case "reload":
                handleReload(sender);
                break;
            case "help":
            default:
                sendHelp(sender);
                break;
        }

        return true;
    }

    private void handleClear(CommandSender sender) {
        if (!sender.hasPermission(PERM_CLEAR) && !sender.hasPermission(PERM_ADMIN)) {
            sender.sendMessage(configManager.getMsgNoPermission());
            return;
        }

        // Send 100 empty lines to clear chat screen for all online players
        StringBuilder blankBuffer = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            blankBuffer.append("§r \n");
        }
        String clearBuffer = blankBuffer.toString();

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.sendMessage(clearBuffer);
        }

        String senderName = (sender instanceof Player) ? sender.getName() : "Console";
        String broadcastMsg = configManager.getMsgChatCleared().replace("{player}", senderName);
        Bukkit.broadcastMessage(broadcastMsg);
    }

    private void handleMuteChat(CommandSender sender) {
        if (!sender.hasPermission(PERM_MUTECHAT) && !sender.hasPermission(PERM_ADMIN)) {
            sender.sendMessage(configManager.getMsgNoPermission());
            return;
        }

        boolean newMuteState = muteManager.toggleMute();
        String senderName = (sender instanceof Player) ? sender.getName() : "Console";
        String broadcastMsg = newMuteState
                ? configManager.getMsgChatMutedOn().replace("{player}", senderName)
                : configManager.getMsgChatMutedOff().replace("{player}", senderName);

        Bukkit.broadcastMessage(broadcastMsg);
    }

    private void handleReload(CommandSender sender) {
        if (!sender.hasPermission(PERM_ADMIN)) {
            sender.sendMessage(configManager.getMsgNoPermission());
            return;
        }

        configManager.loadConfig();
        filterManager.reloadFilters();
        sender.sendMessage(configManager.getMsgConfigReloaded());
    }

    private void sendHelp(CommandSender sender) {
        String prefix = configManager.getPrefix();
        sender.sendMessage(ColorUtils.colorize(prefix + "&e&lPlugin Commands & Chat Moderation Info"));
        sender.sendMessage(ColorUtils.colorize("&8- &f/cg clear &7- Clear public chat."));
        sender.sendMessage(ColorUtils.colorize("&8- &f/cg mutechat &7- Toggle global chat silence."));
        sender.sendMessage(ColorUtils.colorize("&8- &f/cg reload &7- Reload config.yml and filter rules."));
        sender.sendMessage(ColorUtils.colorize("&8- &f/cg help &7- Display this help message."));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> options = new ArrayList<>();
            if (sender.hasPermission(PERM_CLEAR) || sender.hasPermission(PERM_ADMIN)) {
                options.add("clear");
            }
            if (sender.hasPermission(PERM_MUTECHAT) || sender.hasPermission(PERM_ADMIN)) {
                options.add("mutechat");
            }
            if (sender.hasPermission(PERM_ADMIN)) {
                options.add("reload");
            }
            options.add("help");

            return StringUtil.copyPartialMatches(args[0], options, new ArrayList<>());
        }

        return Collections.emptyList();
    }
}
