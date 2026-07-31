# ChatGuard

![Build Status](https://img.shields.io/badge/build-passing-brightgreen.svg)
![Version](https://img.shields.io/badge/version-1.0.0-blue.svg)
![License](https://img.shields.io/badge/license-MIT-green.svg)
![Minecraft](https://img.shields.io/badge/minecraft-1.20%2B-orange.svg)
![Java](https://img.shields.io/badge/java-17%2B-red.svg)

**ChatGuard** is a high-performance, production-grade Minecraft Paper/Spigot 1.20+ plugin designed to provide comprehensive chat moderation, anti-spam protections, intelligent filtering, and real-time staff alerting.

---

## Key Features

- **Intelligent Regex Swear Filtering**: Configurable profanity patterns with customizable replacement masks (or message cancellation).
- **Anti-Advertising Protection**: Advanced IP address and domain detector capable of spotting obfuscated domain variations (e.g., `example (dot) com`, `192.168.1.1`).
- **Caps Percentage Reducer**: Automatically converts messages with excessive capital letters into lowercase or blocks them when exceeding configured thresholds.
- **Anti-Spam & Flood Control**:
  - Per-player thread-safe chat cooldown timers.
  - Repetitive message detection utilizing Levenshtein distance string similarity algorithms.
- **Staff Alerts**: Secretly broadcasts violation details (player name, filter reason, raw unmoderated message) to staff members with the `chatguard.alerts` permission.
- **Server Chat Management**: Ability to toggle global chat silence (`/cg mutechat`) or clear chat history (`/cg clear`).

---

## Commands & Permissions

| Command | Alias | Description | Permission |
| :--- | :--- | :--- | :--- |
| `/chatguard` | `/cg` | Main plugin status & command help | `chatguard.admin` |
| `/chatguard reload` | `/cg reload` | Reloads `config.yml` configuration | `chatguard.admin` |
| `/chatguard clear` | `/cg clear` | Clears public server chat | `chatguard.clear` |
| `/chatguard mutechat` | `/cg mutechat` | Toggles global server chat mute | `chatguard.mutechat` |

### Additional Permissions

| Permission | Description | Default |
| :--- | :--- | :--- |
| `chatguard.alerts` | Receives secret staff alerts for chat violations | `op` |
| `chatguard.bypass.swear` | Bypasses swear filter checks | `op` |
| `chatguard.bypass.advertising` | Bypasses anti-advertising filter checks | `op` |
| `chatguard.bypass.caps` | Bypasses caps percentage checks | `op` |
| `chatguard.bypass.cooldown` | Bypasses chat message cooldown | `op` |
| `chatguard.bypass.spam` | Bypasses duplicate message checks | `op` |
| `chatguard.bypass.mute` | Allows speaking during server chat mute | `op` |

---

## Build Instructions

### Requirements
- **Java Development Kit (JDK)** 17 or higher.
- **Gradle** 8.0+ or **Maven** 3.8+.

### Building with Gradle
```bash
./gradlew build
```
The compiled jar file will be located in `build/libs/ChatGuard-1.0.0.jar`.

### Building with Maven
```bash
mvn clean package
```
The compiled jar file will be located in `target/ChatGuard-1.0.0.jar`.

---

## Configuration (`config.yml`)

The configuration file allows full control over filter sensitivity, regex expressions, messages, and sounds. See `src/main/resources/config.yml` for complete default configuration options.

---

## License

ChatGuard is open-source software licensed under the [MIT License](LICENSE).
