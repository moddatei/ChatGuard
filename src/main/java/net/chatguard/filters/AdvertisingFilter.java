package net.chatguard.filters;

import net.chatguard.config.ConfigManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Filter responsible for detecting IP addresses, domain names, and obfuscated URLs.
 */
public class AdvertisingFilter implements IChatFilter {

    private static final String BYPASS_PERMISSION = "chatguard.bypass.advertising";
    private final ConfigManager configManager;
    private final List<Pattern> compiledPatterns = new ArrayList<>();

    public AdvertisingFilter(ConfigManager configManager) {
        this.configManager = configManager;
        reload();
    }

    @Override
    public FilterResult filter(Player player, String message) {
        if (!isEnabled()) {
            return FilterResult.pass(message);
        }

        if (player != null && player.hasPermission(BYPASS_PERMISSION)) {
            return FilterResult.pass(message);
        }

        // Check if message contains whitelisted domains before running ad detection
        for (String whitelistEntry : configManager.getAdvertisingWhitelist()) {
            if (message.toLowerCase().contains(whitelistEntry.toLowerCase())) {
                return FilterResult.pass(message);
            }
        }

        for (Pattern pattern : compiledPatterns) {
            Matcher matcher = pattern.matcher(message);
            if (matcher.find()) {
                return FilterResult.block("Anti-Advertising", message, configManager.getMsgAdvertisingBlocked());
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
        return configManager.isAdvertisingEnabled();
    }

    @Override
    public void reload() {
        compiledPatterns.clear();
        for (String rawPattern : configManager.getAdvertisingPatterns()) {
            try {
                compiledPatterns.add(Pattern.compile(rawPattern));
            } catch (PatternSyntaxException e) {
                // Ignore invalid regex patterns from config
            }
        }
    }
}
