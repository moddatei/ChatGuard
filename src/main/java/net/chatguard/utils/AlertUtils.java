package net.chatguard.utils;

import net.chatguard.config.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * Utility for dispatching staff notifications to online players with alert permissions.
 */
public final class AlertUtils {

    public static final String ALERT_PERMISSION = "chatguard.alerts";

    private AlertUtils() {
        // Private constructor to prevent instantiation
    }

    /**
     * Sends a discrete staff notification to all online players with alert permissions.
     *
     * @param configManager ConfigManager instance.
     * @param playerName    Name of the violating player.
     * @param reason        Description/type of violation.
     * @param rawMessage    The original raw message attempted by the player.
     */
    public static void notifyStaff(ConfigManager configManager, String playerName, String reason, String rawMessage) {
        if (!configManager.isStaffAlertsEnabled()) {
            return;
        }

        String format = configManager.getStaffAlertFormat();
        String alertMessage = ColorUtils.colorize(
                format.replace("{player}", playerName)
                      .replace("{reason}", reason)
                      .replace("{message}", rawMessage)
        );

        boolean playSound = configManager.isStaffAlertSoundEnabled();
        String soundName = configManager.getStaffAlertSoundName();
        Sound sound = null;

        if (playSound && soundName != null && !soundName.isEmpty()) {
            try {
                sound = Sound.valueOf(soundName.toUpperCase());
            } catch (IllegalArgumentException ignored) {
                // Fallback or ignore invalid sound name
            }
        }

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.hasPermission(ALERT_PERMISSION)) {
                onlinePlayer.sendMessage(alertMessage);
                if (sound != null) {
                    onlinePlayer.playSound(onlinePlayer.getLocation(), sound, 1.0f, 1.0f);
                }
            }
        }

        // Also log to server console for auditing
        Bukkit.getLogger().info("[ChatGuard Alert] " + playerName + " triggered " + reason + ": " + rawMessage);
    }
}
