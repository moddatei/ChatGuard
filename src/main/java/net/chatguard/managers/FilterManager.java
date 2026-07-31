package net.chatguard.managers;

import net.chatguard.config.ConfigManager;
import net.chatguard.filters.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Aggregates and coordinates the execution of chat filters.
 */
public class FilterManager {

    private final List<IChatFilter> filters = new ArrayList<>();

    public FilterManager(ConfigManager configManager) {
        // Register filters in order of priority (Anti-Advertising -> Swear Filter -> Caps Filter)
        filters.add(new AdvertisingFilter(configManager));
        filters.add(new SwearFilter(configManager));
        filters.add(new CapsFilter(configManager));
    }

    /**
     * Evaluates a player's chat message through all registered filters.
     *
     * @param player  The player sending the message.
     * @param message The input chat message.
     * @return Aggregated FilterResult.
     */
    public FilterResult evaluate(Player player, String message) {
        String currentMessage = message;
        boolean wasModified = false;
        String firstViolationReason = null;
        String playerNotification = null;

        for (IChatFilter filter : filters) {
            if (!filter.isEnabled()) {
                continue;
            }

            FilterResult result = filter.filter(player, currentMessage);
            if (result.isBlocked()) {
                // Instantly return block result
                return result;
            }

            if (result.isModified()) {
                wasModified = true;
                currentMessage = result.getProcessedMessage();
                if (firstViolationReason == null) {
                    firstViolationReason = result.getViolationReason();
                }
            }
        }

        if (wasModified) {
            return FilterResult.modify(firstViolationReason, message, currentMessage, playerNotification);
        }

        return FilterResult.pass(message);
    }

    /**
     * Reloads configuration for all registered filters.
     */
    public void reloadFilters() {
        for (IChatFilter filter : filters) {
            filter.reload();
        }
    }

    public List<IChatFilter> getFilters() {
        return Collections.unmodifiableList(filters);
    }
}
