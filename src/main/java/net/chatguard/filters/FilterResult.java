package net.chatguard.filters;

/**
 * Result object representing the outcome of a chat filter evaluation.
 */
public class FilterResult {

    private final boolean blocked;
    private final boolean modified;
    private final String violationReason;
    private final String originalMessage;
    private final String processedMessage;
    private final String playerMessage;

    public FilterResult(boolean blocked, boolean modified, String violationReason,
                        String originalMessage, String processedMessage, String playerMessage) {
        this.blocked = blocked;
        this.modified = modified;
        this.violationReason = violationReason;
        this.originalMessage = originalMessage;
        this.processedMessage = processedMessage;
        this.playerMessage = playerMessage;
    }

    public static FilterResult pass(String message) {
        return new FilterResult(false, false, null, message, message, null);
    }

    public static FilterResult block(String violationReason, String originalMessage, String playerNotification) {
        return new FilterResult(true, false, violationReason, originalMessage, originalMessage, playerNotification);
    }

    public static FilterResult modify(String violationReason, String originalMessage, String processedMessage, String playerNotification) {
        return new FilterResult(false, true, violationReason, originalMessage, processedMessage, playerNotification);
    }

    public boolean isBlocked() {
        return blocked;
    }

    public boolean isModified() {
        return modified;
    }

    public boolean hasViolation() {
        return blocked || modified;
    }

    public String getViolationReason() {
        return violationReason;
    }

    public String getOriginalMessage() {
        return originalMessage;
    }

    public String getProcessedMessage() {
        return processedMessage;
    }

    public String getPlayerMessage() {
        return playerMessage;
    }
}
