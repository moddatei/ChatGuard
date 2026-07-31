package net.chatguard.managers;

import net.chatguard.config.ConfigManager;
import org.bukkit.entity.Player;

/**
 * Manages global server chat silence/mute state.
 */
public class ChatMuteManager {

    private static final String BYPASS_MUTE_PERM = "chatguard.bypass.mute";
    private final ConfigManager configManager;

    public ChatMuteManager(ConfigManager configManager) {
        this.configManager = configManager;
    }

    /**
     * Checks whether global chat is currently muted.
     *
     * @return true if chat is muted, false otherwise.
     */
    public boolean isMuted() {
        return configManager.isChatMuted();
    }

    /**
     * Toggles global chat mute state.
     *
     * @return The new chat muted state.
     */
    public boolean toggleMute() {
        boolean newState = !isMuted();
        configManager.setChatMuted(newState);
        return newState;
    }

    /**
     * Sets global chat mute state.
     *
     * @param muted Desired mute status.
     */
    public void setMuted(boolean muted) {
        configManager.setChatMuted(muted);
    }

    /**
     * Checks if a player can bypass server chat mute.
     *
     * @param player The player to check.
     * @return true if player can speak during mute, false otherwise.
     */
    public boolean canBypassMute(Player player) {
        return player != null && player.hasPermission(BYPASS_MUTE_PERM);
    }
}
