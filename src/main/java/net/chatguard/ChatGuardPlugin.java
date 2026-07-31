package net.chatguard;

import net.chatguard.commands.ChatGuardCommand;
import net.chatguard.config.ConfigManager;
import net.chatguard.listeners.ChatListener;
import net.chatguard.managers.ChatMuteManager;
import net.chatguard.managers.FilterManager;
import net.chatguard.managers.SpamManager;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main JavaPlugin entry point for ChatGuard.
 */
public class ChatGuardPlugin extends JavaPlugin {

    private ConfigManager configManager;
    private FilterManager filterManager;
    private SpamManager spamManager;
    private ChatMuteManager chatMuteManager;

    @Override
    public void onEnable() {
        // 1. Initialize Configuration Manager
        this.configManager = new ConfigManager(this);

        // 2. Initialize Core Managers
        this.filterManager = new FilterManager(configManager);
        this.spamManager = new SpamManager(configManager);
        this.chatMuteManager = new ChatMuteManager(configManager);

        // 3. Register Event Listeners
        getServer().getPluginManager().registerEvents(
                new ChatListener(configManager, filterManager, spamManager, chatMuteManager),
                this
        );

        // 4. Register Command Executor & Tab Completer
        PluginCommand command = getCommand("chatguard");
        if (command != null) {
            ChatGuardCommand cmdExecutor = new ChatGuardCommand(this, configManager, filterManager, chatMuteManager);
            command.setExecutor(cmdExecutor);
            command.setTabCompleter(cmdExecutor);
        } else {
            getLogger().severe("Failed to register /chatguard command! Check plugin.yml configuration.");
        }

        getLogger().info("ChatGuard v" + getDescription().getVersion() + " has been successfully enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("ChatGuard has been disabled.");
    }

    // Getters for Managers
    public ConfigManager getConfigManager() {
        return configManager;
    }

    public FilterManager getFilterManager() {
        return filterManager;
    }

    public SpamManager getSpamManager() {
        return spamManager;
    }

    public ChatMuteManager getChatMuteManager() {
        return chatMuteManager;
    }
}
