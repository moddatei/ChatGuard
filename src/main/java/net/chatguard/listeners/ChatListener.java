package net.chatguard.listeners;

import net.chatguard.config.ConfigManager;
import net.chatguard.filters.FilterResult;
import net.chatguard.managers.ChatMuteManager;
import net.chatguard.managers.FilterManager;
import net.chatguard.managers.SpamManager;
import net.chatguard.utils.AlertUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Listens to player chat events to enforce moderation rules, cooldowns, and filtering.
 */
public class ChatListener implements Listener {

    private final ConfigManager configManager;
    private final FilterManager filterManager;
    private final SpamManager spamManager;
    private final ChatMuteManager muteManager;

    public ChatListener(ConfigManager configManager, FilterManager filterManager,
                        SpamManager spamManager, ChatMuteManager muteManager) {
        this.configManager = configManager;
        this.filterManager = filterManager;
        this.spamManager = spamManager;
        this.muteManager = muteManager;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String originalMessage = event.getMessage();

        // 1. Check Global Server Mute
        if (muteManager.isMuted() && !muteManager.canBypassMute(player)) {
            event.setCancelled(true);
            player.sendMessage(configManager.getMsgChatIsMuted());
            return;
        }

        // 2. Check Chat Cooldown
        double cooldownRemaining = spamManager.checkCooldown(player);
        if (cooldownRemaining > 0) {
            event.setCancelled(true);
            player.sendMessage(configManager.getMsgCooldownWarning().replace("{time}", String.valueOf(cooldownRemaining)));
            return;
        }

        // 3. Check Repetitive Message / Anti-Spam
        if (spamManager.checkSpam(player, originalMessage)) {
            event.setCancelled(true);
            player.sendMessage(configManager.getMsgSpamWarning());
            AlertUtils.notifyStaff(configManager, player.getName(), "Anti-Spam (Repetitive)", originalMessage);
            return;
        }

        // 4. Run Filter Manager
        FilterResult result = filterManager.evaluate(player, originalMessage);

        if (result.isBlocked()) {
            event.setCancelled(true);
            if (result.getPlayerMessage() != null) {
                player.sendMessage(result.getPlayerMessage());
            }
            AlertUtils.notifyStaff(configManager, player.getName(), result.getViolationReason(), originalMessage);
            return;
        }

        if (result.isModified()) {
            event.setMessage(result.getProcessedMessage());
            if (result.getViolationReason() != null) {
                AlertUtils.notifyStaff(configManager, player.getName(), result.getViolationReason(), originalMessage);
            }
        }

        // Record player chat stats upon successful check
        spamManager.recordChatTime(player);
        spamManager.recordPlayerMessage(player, originalMessage);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        spamManager.removePlayer(event.getPlayer());
    }
}
