package net.chatguard.filters;

import net.chatguard.config.ConfigManager;
import org.bukkit.entity.Player;

/**
 * Filter responsible for calculating capital letter ratio and applying caps mitigation.
 */
public class CapsFilter implements IChatFilter {

    private static final String BYPASS_PERMISSION = "chatguard.bypass.caps";
    private final ConfigManager configManager;

    public CapsFilter(ConfigManager configManager) {
        this.configManager = configManager;
    }

    @Override
    public FilterResult filter(Player player, String message) {
        if (!isEnabled()) {
            return FilterResult.pass(message);
        }

        if (player != null && player.hasPermission(BYPASS_PERMISSION)) {
            return FilterResult.pass(message);
        }

        int minLength = configManager.getCapsMinLength();
        if (message == null || message.length() < minLength) {
            return FilterResult.pass(message);
        }

        int capsCount = 0;
        int letterCount = 0;

        for (char c : message.toCharArray()) {
            if (Character.isLetter(c)) {
                letterCount++;
                if (Character.isUpperCase(c)) {
                    capsCount++;
                }
            }
        }

        if (letterCount == 0) {
            return FilterResult.pass(message);
        }

        double capsPercentage = ((double) capsCount / letterCount) * 100.0;
        double maxPercentage = configManager.getCapsMaxPercentage();

        if (capsPercentage > maxPercentage) {
            boolean isBlockMode = "BLOCK".equalsIgnoreCase(configManager.getCapsMode());
            if (isBlockMode) {
                return FilterResult.block("Excessive Caps", message, configManager.getMsgCapsBlocked());
            } else {
                // Lowercase the message
                String lowercased = message.toLowerCase();
                return FilterResult.modify("Excessive Caps", message, lowercased, null);
            }
        }

        return FilterResult.pass(message);
    }

    @Override
    public String getBypassPermission() {
        return BYPASS_PERMISSION;
    }

    @Override
    public boolean isEnabled() {
        return configManager.isCapsEnabled();
    }

    @Override
    public void reload() {
        // No precompiled state required for CapsFilter
    }
}
