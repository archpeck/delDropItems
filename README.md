# DelDropItems Plugin Documentation
Overview
The DelDropItems plugin is designed for Minecraft servers to manage the behavior of dropped items. It allows you to set rules for how long items remain in the world after being dropped by players, broadcast messages to players, and configure custom messages for various events.

Features
Automatic Item Removal: The plugin automatically removes dropped items after a specified time, helping to keep the game environment clean.
Custom Drop Messages: Players receive customizable messages when they drop items, enhancing the gameplay experience.
Configuration Reloading: The configuration settings can be reloaded without needing to restart the server, making adjustments easy.
Broadcasting Messages: The plugin can send messages to all players at specified intervals, keeping everyone informed.
Configuration
The configuration file, config.yml, includes several options:

item-removal-time: This integer value specifies the time in seconds before dropped items are removed from the world. The default value is 60 seconds.

drop-message: A string message sent to the player when they drop an item. The message can include %item_name%, which will be replaced with the name of the item. The default message is &aYou dropped %item_name%.

reload-message: A message sent to the player upon reloading the configuration. The default is &aConfiguration reloaded!.

drop-message-allow: This boolean setting determines whether messages are sent when an item is dropped. The default value is true.

broadcast-interval: An integer specifying how often, in seconds, messages are broadcasted to all players. The default value is 60 seconds.

broadcast-message: A string message sent to all players at the specified broadcast interval. The default message is &bThis is a broadcast message.

broadcast-message-allow: A boolean that controls whether broadcasting messages is allowed. The default value is true.

reload-message-allow: This boolean setting determines if a message is sent upon reloading the configuration. The default value is true.

Commands
The plugin supports the following commands:

/ddi reload: This command reloads the plugin configuration.
/ddi help: This command displays a list of available commands.
Permissions
To control access to commands, the following permissions are available:

delDropItems.reload: Grants permission to reload the configuration.
delDropItems.help: Grants permission to access help information.
Installation
To install the DelDropItems plugin, follow these steps:

Download the plugin jar file.
Place the jar file in the plugins directory of your Minecraft server.
Start or restart the server to generate the default configuration file.
Edit the config.yml file to customize your settings.
Use the /ddi reload command to apply any changes without needing to restart the server.
Troubleshooting
If you encounter issues with the plugin, check the server console for error messages. Ensure that the configuration values are correct and that you have the necessary permissions to use the commands.


# Документация плагина DelDropItems
Обзор
Плагин DelDropItems предназначен для серверов Minecraft и управляет поведением выброшенных предметов. Он позволяет устанавливать правила о том, как долго предметы остаются в мире после того, как игроки их выбрасывают, транслировать сообщения игрокам и настраивать пользовательские сообщения для различных событий.

Особенности
Автоматическое удаление предметов: Плагин автоматически удаляет выброшенные предметы после заданного времени, что помогает поддерживать чистоту игрового окружения.
Настраиваемые сообщения о выбросе: Игроки получают настраиваемые сообщения, когда они выбрасывают предметы, что улучшает игровой опыт.
Перезагрузка конфигурации: Настройки конфигурации можно перезагружать без необходимости перезапуска сервера, что облегчает внесение изменений.
Трансляция сообщений: Плагин может отправлять сообщения всем игрокам через заданные интервалы, информируя их о событиях.
Конфигурация
Конфигурационный файл, config.yml, включает в себя несколько параметров:

item-removal-time: Это целочисленное значение указывает время в секундах, через которое выброшенные предметы будут удалены из мира. Значение по умолчанию — 60 секунд.

drop-message: Строка сообщения, отправляемая игроку, когда он выбрасывает предмет. Сообщение может включать %item_name%, которое будет заменено названием предмета. Сообщение по умолчанию: &aВы выбросили %item_name%.

reload-message: Сообщение, отправляемое игроку при перезагрузке конфигурации. Значение по умолчанию: &aКонфигурация перезагружена!.

drop-message-allow: Эта логическая настройка определяет, отправляются ли сообщения при выбросе предмета. Значение по умолчанию — true.

broadcast-interval: Целое число, указывающее, как часто, в секундах, сообщения транслируются всем игрокам. Значение по умолчанию — 60 секунд.

broadcast-message: Строка сообщения, отправляемая всем игрокам через заданные интервалы. Сообщение по умолчанию: &bЭто сообщение трансляции.

broadcast-message-allow: Логическая настройка, контролирующая, разрешена ли трансляция сообщений. Значение по умолчанию — true.

reload-message-allow: Эта логическая настройка определяет, отправляется ли сообщение при перезагрузке конфигурации. Значение по умолчанию — true.

Команды
Плагин поддерживает следующие команды:

/ddi reload: Эта команда перезагружает конфигурацию плагина.
/ddi help: Эта команда отображает список доступных команд.
Разрешения
Для контроля доступа к командам доступны следующие разрешения:

delDropItems.reload: Предоставляет разрешение на перезагрузку конфигурации.
delDropItems.help: Предоставляет разрешение на доступ к информации о помощи.
Установка
Чтобы установить плагин DelDropItems, выполните следующие шаги:

Скачайте файл .jar плагина.
Поместите файл .jar в директорию plugins вашего сервера Minecraft.
Запустите или перезапустите сервер, чтобы сгенерировать файл конфигурации по умолчанию.
Отредактируйте файл config.yml, чтобы настроить параметры по своему усмотрению.
Используйте команду /ddi reload, чтобы применить изменения без перезапуска сервера.
Устранение неполадок
Если вы столкнулись с проблемами, проверьте консоль сервера на наличие сообщений об ошибках. Убедитесь, что значения конфигурации корректны и у вас есть необходимые разрешения для использования команд.
