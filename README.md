<p align="center">
  <b><a>Welcome to the BetterClaims' repository!</a></b>
</p>

## Web configuration editor

[Open the BetterClaims editor](https://www.minecentral.net/editor/betterclaims) to start with defaults or upload config.yml. Edit claim limits, enabled worlds, global/player settings, and default flags.

1. Run `/betterclaims editor` on your server and open the private link it returns.
2. Edit the form or YAML and choose **Save and get sync command**.
3. Run `/betterclaims editorsync <token>` in Minecraft to apply that saved version.

Console and players with `betterclaims.editor` (default: op) can use both commands. No account or premium is required for editing, downloading, or command sync. Upload links last 24 hours; each sync command works once and expires after 15 minutes. Keep links and commands private.

Sync validates config.yml, saves a backup in `editor-backups`, then replaces the file and reloads the plugin. Nothing changes until the sync command runs. If reload fails after writing, the new file remains in place and Minecraft reports the error.

Shared behavior, deployment, and verification are documented in the [webapp repository](https://github.com/RonanPlugins/BetterRTP-Webapp/blob/main/docs/web-editors/betterclaims.md).

## Wheres the Wiki?
The wiki is available [here](../../wiki)!
    
<p align="center">
  <b>Chat with us on Discord</b><br/>
  <a href="https://discord.gg/8Kt4wKm"><img src="https://img.shields.io/discord/182633513474850818.svg?longCache=true&style=flat-square&label=Discord" alt="Discord" /></a><br/>
  <b>Have a Suggestion? Make an issue!</b><br/>
  <a href="../../issues"><img src="https://img.shields.io/github/issues-raw/SuperRonanCraft/Pueblos.svg?longCache=true&style=flat-square&label=Issues" alt="GitHub issues" /></a><br/>
  <br/>
  <a href="https://www.spigotmc.org/resources/91255/">Thank you for viewing the Wiki for Pueblos!</a><br/>
  <i><a>Did this wiki help you out? Please give it a <b>Star</b> so I know it's getting use!</a></i><br/>
  <br/>
  <b><i><a href="https://www.spigotmc.org/resources/authors/superronancraft.13025/">Check out my other plugins!</a></i></b>
</p>
