package net.chatguard.managers;

import net.chatguard.config.ConfigManager;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe manager handling chat cooldowns and repetitive message detection.
 */
public class SpamManager {

    private static final String BYPASS_COOLDOWN_PERM = "chatguard.bypass.cooldown";
    private static final String BYPASS_SPAM_PERM = "chatguard.bypass.spam";

    private final ConfigManager configManager;
    private final Map<UUID, Long> lastChatTimes = new ConcurrentHashMap<>();
    private final Map<UUID, PlayerMessageEntry> lastMessages = new ConcurrentHashMap<>();

    public SpamManager(ConfigManager configManager) {
        this.configManager = configManager;
    }

    /**
     * Checks if a player is on chat cooldown.
     *
     * @param player The player to check.
     * @return Remaining cooldown time in seconds, or 0.0 if not on cooldown.
     */
    public double checkCooldown(Player player) {
        if (!configManager.isCooldownEnabled() || player.hasPermission(BYPASS_COOLDOWN_PERM)) {
            return 0.0;
        }

        UUID uuid = player.getUniqueId();
        Long lastTime = lastChatTimes.get(uuid);
        if (lastTime == null) {
            return 0.0;
        }

        long cooldownMillis = (long) (configManager.getCooldownSeconds() * 1000);
        long elapsed = System.currentTimeMillis() - lastTime;

        if (elapsed < cooldownMillis) {
            double remainingSeconds = (cooldownMillis - elapsed) / 1000.0;
            return Math.round(remainingSeconds * 10.0) / 10.0;
        }

        return 0.0;
    }

    /**
     * Updates the last chat timestamp for a player.
     *
     * @param player The player.
     */
    public void recordChatTime(Player player) {
        lastChatTimes.put(player.getUniqueId(), System.currentTimeMillis());
    }

    /**
     * Checks if a player's message is repetitive/similar to recently sent messages.
     *
     * @param player  The player sending the message.
     * @param message The new chat message.
     * @return true if detected as repetitive spam, false otherwise.
     */
    public boolean checkSpam(Player player, String message) {
        if (!configManager.isSpamEnabled() || player.hasPermission(BYPASS_SPAM_PERM)) {
            return false;
        }

        UUID uuid = player.getUniqueId();
        PlayerMessageEntry previousEntry = lastMessages.get(uuid);
        long now = System.currentTimeMillis();
        long historyWindowMillis = configManager.getSpamHistorySeconds() * 1000L;

        if (previousEntry != null) {
            long elapsed = now - previousEntry.getTimestamp();
            if (elapsed <= historyWindowMillis) {
                double similarity = calculateSimilarity(previousEntry.getMessage(), message);
                if (similarity >= configManager.getSpamSimilarityPercentage()) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Records a player's recent message content for spam checking.
     *
     * @param player  The player.
     * @param message The message text.
     */
    public void recordPlayerMessage(Player player, String message) {
        lastMessages.put(player.getUniqueId(), new PlayerMessageEntry(message, System.currentTimeMillis()));
    }

    /**
     * Removes player records upon disconnect.
     *
     * @param player The player quitting.
     */
    public void removePlayer(Player player) {
        UUID uuid = player.getUniqueId();
        lastChatTimes.remove(uuid);
        lastMessages.remove(uuid);
    }

    /**
     * Calculates string similarity percentage (0.0 to 100.0) using Levenshtein distance.
     */
    public double calculateSimilarity(String s1, String s2) {
        if (s1 == null || s2 == null) {
            return 0.0;
        }

        String str1 = s1.toLowerCase().trim();
        String str2 = s2.toLowerCase().trim();

        if (str1.equals(str2)) {
            return 100.0;
        }

        int maxLength = Math.max(str1.length(), str2.length());
        if (maxLength == 0) {
            return 100.0;
        }

        int distance = computeLevenshteinDistance(str1, str2);
        return (1.0 - ((double) distance / maxLength)) * 100.0;
    }

    private int computeLevenshteinDistance(String s1, String s2) {
        int[] costs = new int[s2.length() + 1];
        for (int j = 0; j <= s2.length(); j++) {
            costs[j] = j;
        }

        for (int i = 1; i <= s1.length(); i++) {
            costs[0] = i;
            int nw = i - 1;
            for (int j = 1; j <= s2.length(); j++) {
                int cj = Math.min(
                        1 + Math.min(costs[j], costs[j - 1]),
                        s1.charAt(i - 1) == s2.charAt(j - 1) ? nw : nw + 1
                );
                nw = costs[j];
                costs[j] = cj;
            }
        }

        return costs[s2.length()];
    }

    private static class PlayerMessageEntry {
        private final String message;
        private final long timestamp;

        public PlayerMessageEntry(String message, long timestamp) {
            this.message = message;
            this.timestamp = timestamp;
        }

        public String getMessage() {
            return message;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }
}
