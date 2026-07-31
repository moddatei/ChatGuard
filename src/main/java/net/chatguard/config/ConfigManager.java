package net.chatguard.config;

import net.chatguard.utils.ColorUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.List;

/**
 * Manages plugin configuration loading, reloading, and typed value extraction.
 */
public class ConfigManager {

    private final JavaPlugin plugin;
    private FileConfiguration config;

    // Cached Config Options
    private String prefix;
    
    // Swear Filter
    private boolean swearEnabled;
    private String swearMode;
    private String censorReplacement;
    private List<String> swearPatterns;

    // Advertising Filter
    private boolean advertisingEnabled;
    private String advertisingMode;
    private List<String> advertisingWhitelist;
    private List<String> advertisingPatterns;

    // Caps Filter
    private boolean capsEnabled;
    private int capsMinLength;
    private double capsMaxPercentage;
    private String capsMode;

    // Cooldown & Spam
    private boolean cooldownEnabled;
    private double cooldownSeconds;

    private boolean spamEnabled;
    private int spamHistorySeconds;
    private double spamSimilarityPercentage;

    // Chat Muted State
    private boolean chatMuted;

    // Staff Alerts
    private boolean staffAlertsEnabled;
    private boolean staffAlertSoundEnabled;
    private String staffAlertSoundName;
    private String staffAlertFormat;

    // Messages
    private String msgNoPermission;
    private String msgChatIsMuted;
    private String msgCooldownWarning;
    private String msgSpamWarning;
    private String msgSwearBlocked;
    private String msgAdvertisingBlocked;
    private String msgCapsBlocked;
    private String msgChatCleared;
    private String msgChatMutedOn;
    private String msgChatMutedOff;
    private String msgConfigReloaded;
    private String msgUsage;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    /**
     * Loads or reloads the plugin configuration file.
     */
    public synchronized void loadConfig() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        this.config = plugin.getConfig();

        // Load Global Values
        this.prefix = ColorUtils.colorize(config.getString("prefix", "&8[&c&lChatGuard&8] "));

        // Swear Filter Settings
        this.swearEnabled = config.getBoolean("swear-filter.enabled", true);
        this.swearMode = config.getString("swear-filter.mode", "CENSOR");
        this.censorReplacement = config.getString("swear-filter.censor-replacement", "****");
        this.swearPatterns = config.getStringList("swear-filter.patterns");

        // Advertising Filter Settings
        this.advertisingEnabled = config.getBoolean("advertising-filter.enabled", true);
        this.advertisingMode = config.getString("advertising-filter.mode", "BLOCK");
        this.advertisingWhitelist = config.getStringList("advertising-filter.whitelist");
        this.advertisingPatterns = config.getStringList("advertising-filter.patterns");

        // Caps Filter Settings
        this.capsEnabled = config.getBoolean("caps-filter.enabled", true);
        this.capsMinLength = config.getInt("caps-filter.min-message-length", 6);
        this.capsMaxPercentage = config.getDouble("caps-filter.max-caps-percentage", 60.0);
        this.capsMode = config.getString("caps-filter.mode", "LOWERCASE");

        // Cooldown & Anti-Spam
        this.cooldownEnabled = config.getBoolean("cooldown.enabled", true);
        this.cooldownSeconds = config.getDouble("cooldown.seconds", 3.0);

        this.spamEnabled = config.getBoolean("spam-filter.enabled", true);
        this.spamHistorySeconds = config.getInt("spam-filter.history-seconds", 15);
        this.spamSimilarityPercentage = config.getDouble("spam-filter.similarity-percentage", 80.0);

        // Chat Muted Initial State
        this.chatMuted = config.getBoolean("chat-muted", false);

        // Staff Alerts
        this.staffAlertsEnabled = config.getBoolean("staff-alerts.enabled", true);
        this.staffAlertSoundEnabled = config.getBoolean("staff-alerts.play-sound", true);
        this.staffAlertSoundName = config.getString("staff-alerts.sound-name", "BLOCK_NOTE_BLOCK_PLING");
        this.staffAlertFormat = config.getString("staff-alerts.format", "&c&l[ALERT] &f{player} &7triggered &e{reason}&7: &f{message}");

        // Messages
        this.msgNoPermission = getFormattedMessage("messages.no-permission", "&cYou do not have permission to execute this command.");
        this.msgChatIsMuted = getFormattedMessage("messages.chat-is-muted", "&cServer chat is currently muted. You cannot send messages.");
        this.msgCooldownWarning = getFormattedMessage("messages.cooldown-warning", "&cPlease wait &e{time}s &cbefore sending another message.");
        this.msgSpamWarning = getFormattedMessage("messages.spam-warning", "&cPlease refrain from repeating similar messages.");
        this.msgSwearBlocked = getFormattedMessage("messages.swear-blocked", "&cYour message was blocked because it contained prohibited language.");
        this.msgAdvertisingBlocked = getFormattedMessage("messages.advertising-blocked", "&cAdvertising IP addresses or domain links is strictly prohibited.");
        this.msgCapsBlocked = getFormattedMessage("messages.caps-blocked", "&cYour message contained too many capital letters.");
        this.msgChatCleared = getFormattedMessage("messages.chat-cleared", "&aServer chat has been cleared by &e{player}&a.");
        this.msgChatMutedOn = getFormattedMessage("messages.chat-muted-on", "&cServer chat has been &lMUTED &cby &e{player}&c.");
        this.msgChatMutedOff = getFormattedMessage("messages.chat-muted-off", "&aServer chat has been &lUNMUTED &aby &e{player}&a.");
        this.msgConfigReloaded = getFormattedMessage("messages.config-reloaded", "&aChatGuard configuration reloaded successfully!");
        this.msgUsage = getFormattedMessage("messages.usage", "&eUsage: /chatguard [clear|mutechat|reload|help]");
    }

    private String getFormattedMessage(String path, String fallback) {
        return ColorUtils.colorize(prefix + config.getString(path, fallback));
    }

    /**
     * Saves the current chat muted state back to file.
     */
    public synchronized void setChatMuted(boolean muted) {
        this.chatMuted = muted;
        config.set("chat-muted", muted);
        plugin.saveConfig();
    }

    // Getters
    public String getPrefix() { return prefix; }
    
    public boolean isSwearEnabled() { return swearEnabled; }
    public String getSwearMode() { return swearMode; }
    public String getCensorReplacement() { return censorReplacement; }
    public List<String> getSwearPatterns() { return Collections.unmodifiableList(swearPatterns); }

    public boolean isAdvertisingEnabled() { return advertisingEnabled; }
    public String getAdvertisingMode() { return advertisingMode; }
    public List<String> getAdvertisingWhitelist() { return Collections.unmodifiableList(advertisingWhitelist); }
    public List<String> getAdvertisingPatterns() { return Collections.unmodifiableList(advertisingPatterns); }

    public boolean isCapsEnabled() { return capsEnabled; }
    public int getCapsMinLength() { return capsMinLength; }
    public double getCapsMaxPercentage() { return capsMaxPercentage; }
    public String getCapsMode() { return capsMode; }

    public boolean isCooldownEnabled() { return cooldownEnabled; }
    public double getCooldownSeconds() { return cooldownSeconds; }

    public boolean isSpamEnabled() { return spamEnabled; }
    public int getSpamHistorySeconds() { return spamHistorySeconds; }
    public double getSpamSimilarityPercentage() { return spamSimilarityPercentage; }

    public boolean isChatMuted() { return chatMuted; }

    public boolean isStaffAlertsEnabled() { return staffAlertsEnabled; }
    public boolean isStaffAlertSoundEnabled() { return staffAlertSoundEnabled; }
    public String getStaffAlertSoundName() { return staffAlertSoundName; }
    public String getStaffAlertFormat() { return staffAlertFormat; }

    public String getMsgNoPermission() { return msgNoPermission; }
    public String getMsgChatIsMuted() { return msgChatIsMuted; }
    public String getMsgCooldownWarning() { return msgCooldownWarning; }
    public String getMsgSpamWarning() { return msgSpamWarning; }
    public String getMsgSwearBlocked() { return msgSwearBlocked; }
    public String getMsgAdvertisingBlocked() { return msgAdvertisingBlocked; }
    public String getMsgCapsBlocked() { return msgCapsBlocked; }
    public String getMsgChatCleared() { return msgChatCleared; }
    public String getMsgChatMutedOn() { return msgChatMutedOn; }
    public String getMsgChatMutedOff() { return msgChatMutedOff; }
    public String getMsgConfigReloaded() { return msgConfigReloaded; }
    public String getMsgUsage() { return msgUsage; }
}
