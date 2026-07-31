package net.chatguard.utils;

import org.bukkit.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for handling color translation including legacy '&' codes
 * and hex color codes formatting.
 */
public final class ColorUtils {

    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

    private ColorUtils() {
        // Private constructor to prevent instantiation
    }

    /**
     * Translates alternate color codes and hex strings into formatted Minecraft text.
     *
     * @param message Raw input text containing color codes.
     * @return Formatted string with Bukkit ChatColor values.
     */
    public static String colorize(String message) {
        if (message == null || message.isEmpty()) {
            return "";
        }

        // Process Hex color codes (&#RRGGBB)
        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuilder builder = new StringBuilder();
        while (matcher.find()) {
            String hexCode = matcher.group(1);
            StringBuilder replacement = new StringBuilder("§x");
            for (char c : hexCode.toCharArray()) {
                replacement.append('§').append(c);
            }
            matcher.appendReplacement(builder, replacement.toString());
        }
        matcher.appendTail(builder);

        // Process legacy ChatColor codes ('&')
        return ChatColor.translateAlternateColorCodes('&', builder.toString());
    }
}
