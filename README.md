# DelDropItems Plugin Documentation
![Java](https://img.shields.io/badge/Java-17-red?style=flat)
![Version](https://img.shields.io/badge/Version-v1.1.2-blue?style=flat)
![Minecraft](https://img.shields.io/badge/Minecraft-1.12+-green?style=flat)
![Spigot](https://img.shields.io/badge/Downloads-400+-yellow?style=flat)

## Overview

The DelDropItems plugin is designed for Minecraft servers to manage the behavior of dropped items. It allows server administrators to set rules for how long items remain in the world after being dropped by players, broadcast messages to all players, and configure custom messages for various events.

## Features

- **Automatic Item Removal**: The plugin automatically removes dropped items after a specified time, helping to maintain a clean game environment.
- **Custom Drop Messages**: Players receive customizable messages when they drop items, enhancing the gameplay experience.
- **Configuration Reloading**: The configuration settings can be reloaded without needing to restart the server, making adjustments easy.
- **Broadcasting Messages**: The plugin can send messages to all players at specified intervals, keeping everyone informed.
- **Logging of Item Removals**: All removed items are logged with the player’s name and the item’s type, allowing for easy tracking.

## Configuration

The configuration file, `config.yml`, includes several options:

- **item-removal-time**: This integer value specifies the time (in seconds) before dropped items are removed from the world. The default value is `60` seconds.
    
- **drop-message-allow**: This boolean setting determines whether messages are sent when an item is dropped. The default value is `true`.
    
- **broadcast-interval**: An integer specifying how often (in seconds) messages are broadcasted to all players. The default value is `60` seconds.
    
- **broadcast-message-allow**: A boolean that controls whether broadcasting messages is allowed. The default value is `true`.
    
- **reload-message-allow**: This boolean setting determines if a message is sent upon reloading the configuration. The default value is `true`.
    
- **language**: Specifies the language in which messages will be displayed. Available values: `en` or `ru`. The default value is `en`.

Messages are changed in the `messages_ru` and `messages_en` files according to the selected language.

- **drop-message**: A string message sent to the player when they drop an item. The message can include `%item_name%`, which will be replaced with the name of the item. The default message is `[§aDelDropItems§f] §eYou dropped: %item_name%!`
    
- **reload-message**: A message sent to the player upon reloading the configuration. The default is `[§aDelDropItems§f] §ePlugin configuration has been successfully reloaded!.`
    
- **broadcast-message**: A string message sent to all players at the specified broadcast interval. The default message is `[§aDelDropItems§f] §cItem removal will occur in §l5 minutes§r!`

Changing the language only affects the messages sent by the plugin and does not change the language of the Minecraft interface.  
Upon server reload, the language settings are retained, and the last set language will be used.

## Commands

The plugin supports the following commands:

- **/ddi reload**: This command reloads the plugin configuration.
    
- **/ddi help**: This command displays a list of available commands.

## Permissions

To control access to commands, the following permissions are available:

- **delDropItems.reload**: Grants permission to reload the configuration.
    
- **delDropItems.help**: Grants permission to access help information.

## Installation

To install the DelDropItems plugin, follow these steps:

1. Download the plugin JAR file.
2. Place the JAR file in the `plugins` directory of your Minecraft server.
3. Start or restart the server to generate the default configuration file.
4. Edit the `config.yml` file to customize your settings.
5. Use the `/ddi reload` command to apply any changes without needing to restart the server.

## Troubleshooting

If you encounter issues with the plugin, check the server console for error messages. Ensure that the configuration values are correct and that you have the necessary permissions to use the commands. Additionally, if you do not see expected messages or behavior, verify the plugin is loaded correctly and that all dependencies are satisfied.
