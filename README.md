# DelDropItems

![Java](https://img.shields.io/badge/Java-17-red?style=flat)
![Version](https://img.shields.io/badge/Version-v1.2.0-blue?style=flat)
![Minecraft](https://img.shields.io/badge/Minecraft-1.17+-green?style=flat)

[English](README.md) | [Русский](README_RU.md) <!--gh-->

DelDropItems automatically removes items lying on the ground after a set time. Less clutter in your world, less load on your server. It is lightweight, needs no dependencies and is configured with a single file.

## Features

- Automatic removal of dropped items after a configurable time
- Cleanup mode: only items dropped by players, or every item in the world
- Ignore list: chosen materials (diamonds, shulker boxes, ...) are never removed
- Optional: keep items that have a custom name
- Optional: keep items while a player is nearby
- Disable the plugin in selected worlds
- Periodic chat reminder with `%minutes%` / `%seconds%` placeholders
- Message to the player about the item they dropped
- Removal log: who dropped what, where and when
- Instant cleanup with `/ddi clear`
- Messages in 8 languages (English, Russian, French, German, Spanish, Italian, Chinese, Japanese), all texts editable

## Requirements

- Spigot or Paper (or a fork of them), Minecraft 1.17 or newer (older versions are not supported)
- Java 17 or newer
- No other plugins are required

## Installation

1. Download the latest `DelDropItems-x.y.z.jar`.
2. Put the file into your server's `plugins` folder.
3. Start (or restart) the server. The plugin creates `plugins/DelDropItems/` with `config.yml` and a `messages` folder.
4. Open `plugins/DelDropItems/config.yml` and adjust it (see [Configuration](#configuration)).
5. Apply the changes without a restart: `/ddi reload`.

To update, stop the server, replace the old JAR with the new one and start the server again. Your `config.yml` and message files are kept. New options that are missing from your old `config.yml` use their default values; copy them from the new default config if you want to change them.

## Quick start

The default settings work out of the box: items thrown by players are removed after 5 minutes, and a reminder is broadcast every 5 minutes. A typical customisation looks like this:

```yaml
language: "en"
item-removal-time: 120
only-player-dropped: true
ignored-materials: [DIAMOND, NETHERITE_INGOT, SHULKER_BOX]
disabled-worlds: [world_nether]
```

## Commands

| Command | Description | Permission |
|---|---|---|
| `/ddi help` | Show the list of commands you may use | `delDropItems.help` |
| `/ddi reload` | Reload `config.yml` and the message file, restart the timers | `delDropItems.reload` |
| `/ddi clear` | Remove all matching items right now, ignoring their age | `delDropItems.clear` |
| `/ddi view-logs` | Show the last 10 removals | `delDropItems.view-logs` |

The alias `/delDropItems` works too.

## Permissions

All permissions are granted to operators by default.

| Permission | Description |
|---|---|
| `delDropItems.use` | Allows using the `/ddi` command at all |
| `delDropItems.help` | `/ddi help` |
| `delDropItems.reload` | `/ddi reload` |
| `delDropItems.clear` | `/ddi clear` |
| `delDropItems.view-logs` | `/ddi view-logs` |
| `delDropItems.*` | All of the above |

A player needs both `delDropItems.use` and the permission of the subcommand. Example with LuckPerms: `/lp user Steve permission set delDropItems.use true` and `/lp user Steve permission set delDropItems.clear true`.

## Configuration

File: `plugins/DelDropItems/config.yml`. After editing, run `/ddi reload`.

### General

| Option | Default | Description |
|---|---|---|
| `language` | `en` | Language of the plugin messages: `en`, `ru`, `fr`, `de`, `es`, `it`, `cn`, `ja`. Applies to chat messages, console output and the removal log, not to the Minecraft interface. An unknown value falls back to English. |

### Removal

| Option | Default | Description |
|---|---|---|
| `item-removal-time` | `300` | Seconds an item may lie on the ground before it is removed. Minimum `1`. |
| `only-player-dropped` | `true` | `true`: only items thrown by players (Q key or dragged out of the inventory). `false`: every item entity, including mob drops, broken blocks and death drops. |
| `distance-player-to-object` | `0` | Distance in blocks. While a player is closer than this to an item, the item is **not** removed. `0` turns the check off. |
| `ignored-materials` | `[]` | Materials that are never removed, e.g. `[DIAMOND, NETHERITE_INGOT, SHULKER_BOX]`. Use the names from the Spigot `Material` list. Unknown names are reported in the console. |
| `ignore-named-items` | `false` | `true`: items with a custom name (for example renamed in an anvil) are never removed. |
| `disabled-worlds` | `[]` | World names where the plugin does nothing, e.g. `[world_nether, world_the_end]`. Names are case-sensitive. |

### Messages and logging

| Option | Default | Description |
|---|---|---|
| `drop-message-allow` | `true` | Send the player a message when they drop an item. |
| `reload-message-allow` | `true` | Send a message after `/ddi reload`. |
| `broadcast-message-allow` | `true` | Periodically broadcast the reminder to all players. |
| `broadcast-interval` | `300` | Seconds between reminders. `0` turns them off. |
| `log-removals` | `true` | Write every removal to `plugins/DelDropItems/logs/item_removal_log.txt`. |

### Notes that are worth knowing

- **Server despawn.** Spigot and Paper remove items by themselves after 6000 ticks (300 seconds). `item-removal-time` values above `300` only take effect if you also raise `item-despawn-rate` in `spigot.yml`.
- **Loaded chunks only.** Items age and are removed only in chunks that are loaded.
- **Who counts as "dropped by a player".** The plugin uses the thrower stored on the item. Items from mobs, blocks, hoppers or commands have no thrower, so with `only-player-dropped: true` they are left alone.
- **`/ddi clear`** ignores age and the nearby-player check, but still respects `only-player-dropped`, `ignored-materials`, `ignore-named-items` and `disabled-worlds`.

## Messages

Message files are stored in `plugins/DelDropItems/messages/messages_<language>.yml`. They are created from the built-in defaults the first time a language is used and are never overwritten afterwards, so you can edit them freely. If a key is missing from your file, the built-in text is used.

Colour codes: use `§` or `&`, for example `&a` for green and `&l` for bold.

| Key | Used for | Placeholders |
|---|---|---|
| `prefix` | Prefix in front of `no-permission`, `unknown-command`, `clear-message` and `logs-empty`. `broadcast-message`, `drop-message` and `reload-message` carry their own prefix in the text; change it there | |
| `broadcast-message` | Periodic reminder | `%minutes%`, `%seconds%` |
| `drop-message` | Message to the player who dropped an item | `%item_name%` |
| `reload-message` | After `/ddi reload` | |
| `clear-message` | After `/ddi clear` | `%count%` |
| `no-permission` | Missing permission | |
| `unknown-command` | Unknown subcommand | |
| `logs-header`, `logs-empty` | `/ddi view-logs` output | |
| `help-header`, `help-reload`, `help-clear`, `help-logs`, `help-help` | `/ddi help` output | |
| `console-enabled` | Console line on startup | `%version%` |
| `console-unknown-material`, `console-language-missing`, `console-folder-error`, `console-file-error`, `console-log-error` | Console warnings and errors | `%name%`, `%language%`, `%error%` |
| `log-removal` | One line of the removal log | `%time%`, `%item%`, `%amount%`, `%player%`, `%world%`, `%x%`, `%y%`, `%z%` |
| `log-unknown-player` | Name used in the log when the thrower is unknown | |

`%minutes%` is the removal time in whole minutes (at least 1). For removal times under a minute use `%seconds%`.

## Removal log

If `log-removals` is on, every removed item is appended to `plugins/DelDropItems/logs/item_removal_log.txt`:

```
[2026-11-07 18:42:10] Item: DIAMOND x3, dropped by: Steve, world: world (120, 64, -45) has been removed.
```

The log is written in the language selected in `language`. If the thrower is not known, `unknown` (translated) is written instead of the name. If the player is offline, their UUID is written. The last 10 lines are available in game with `/ddi view-logs`.

## FAQ

**Items are not removed.**
Check that: the item was thrown by a player (or `only-player-dropped` is `false`); its material is not in `ignored-materials`; the world is not in `disabled-worlds`; no player is within `distance-player-to-object`; the item is in a loaded chunk; enough time has passed.

**Items disappear faster than `item-removal-time`.**
Spigot/Paper despawn items after 300 seconds by default, and other plugins may also clean items. Check `spigot.yml` and your other cleanup plugins.

**I get "Unknown material" in the console.**
A name in `ignored-materials` does not exist in your Minecraft version. Names are not case-sensitive (`diamond` works too); check the spelling against the Spigot `Material` list for your server version, e.g. `NETHERITE_INGOT` exists only since 1.16.

**Messages are not in my language.**
Check `language` in `config.yml` and run `/ddi reload`. If you changed the language after editing a message file, the new language's file is created separately: `messages_<language>.yml`.

**The plugin does not load.**
Check the console for the exact error. Make sure the server runs Java 17 or newer.

## Support

Found a bug or have an idea? Open an [issue on GitHub](https://github.com/archpeck/delDropItems/issues) or leave a comment on the Spigot page. Please include your server version, the plugin version and the console output.

## License

See the [LICENSE](LICENSE) file.
