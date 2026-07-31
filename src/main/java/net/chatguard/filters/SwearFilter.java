package net.chatguard.filters;

import net.chatguard.config.ConfigManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Filter responsible for detecting profanity using compiled Regular Expressions.
 */
public class SwearFilter implements IChatFilter {

    private static final String BYPASS_PERMISSION = "chatguard.bypass.swear";
    private final ConfigManager configManager;
    private final List<Pattern> compiledPatterns = new ArrayList<>();

    public SwearFilter(ConfigManager configManager) {
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

        boolean matched = false;
        String currentMessage = message;
        String censorReplacement = configManager.getCensorReplacement();
        boolean isBlockMode = "BLOCK".equalsIgnoreCase(configManager.getSwearMode());

        for (Pattern pattern : compiledPatterns) {
            Matcher matcher = pattern.matcher(currentMessage);
            if (matcher.find()) {
                matched = true;
                if (isBlockMode) {
                    return FilterResult.block("Swear Filter", message, configManager.getMsgSwearBlocked());
                } else {
                    currentMessage = matcher.replaceAll(censorReplacement);
                }
            }
        }

        if (matched) {
            return FilterResult.modify("Swear Filter", message, currentMessage, null);
        }

        return FilterResult.pass(message);
    }

    @Override
    public String getBypassPermission() {
        return BYPASS_PERMISSION;
    }

    @Override
    public boolean isEnabled() {
        return configManager.isSwearEnabled();
    }

    @Override
    public void reload() {
        compiledPatterns.clear();
        for (String rawPattern : configManager.getSwearPatterns()) {
            try {
                compiledPatterns.add(Pattern.compile(rawPattern));
            } catch (PatternSyntaxException e) {
                // Ignore invalid regex patterns from config
            }
        }
    }
}
