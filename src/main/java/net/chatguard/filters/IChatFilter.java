package net.chatguard.filters;

import org.bukkit.entity.Player;

/**
 * Interface contract for ChatGuard chat filters.
 */
public interface IChatFilter {

    /**
     * Filters and evaluates a player's chat message.
     *
     * @param player  The player sending the message.
     * @param message The raw message text to evaluate.
     * @return FilterResult object containing evaluation outcome.
     */
    FilterResult filter(Player player, String message);

    /**
     * Gets the permission required to bypass this filter.
     *
     * @return Permission node string.
     */
    String getBypassPermission();

    /**
     * Checks if this filter is currently enabled in configuration.
     *
     * @return true if enabled, false otherwise.
     */
    boolean isEnabled();

    /**
     * Reloads configuration options and precompiled patterns.
     */
    void reload();
}
