<p align="center">
  <b><a>Welcome to the BetterClaims' repository!</a></b>
</p>

## Server compatibility and builds

One JAR targets Spigot/Paper from Minecraft 1.8.8 onward and modern Folia. Folia
itself does not exist for Minecraft 1.8.8. The plugin compiles against the 1.8.8
API with Java 8 bytecode; the server still needs the Java version required by
its Minecraft release. FoliaLib is bundled and relocated inside the JAR, so no
separate scheduler plugin or Folia-specific download is required.

```sh
mvn clean package
mvn -f tests/editor/pom.xml clean test
```

Deploy `target/BetterClaims-3.0.4.jar`. The build removes Maven's unshaded backup JAR.
Build with JDK 17 or newer. To run the tests on a Java 8 runtime while building
with a newer JDK, add `-Djvm=/path/to/java8/bin/java` to the second command.

Player work uses the entity scheduler, world reads use the owning region, and
teleports use the platform's asynchronous teleport implementation. Shared plugin
state is serialized across commands, events, scheduled callbacks, and reloads;
never block waiting for another region while holding the state lock. Database
mutations remain serialized too, so slow storage can delay plugin operations.
Optional integrations need their own server-compatible versions.

Verification: the automated suite covers scheduling ownership, disconnects,
shutdown, concurrent state updates, editor validation, and loading the packaged
scheduler with the 1.8.8 API. The packaged scheduler was exercised on Java 8;
direct Bukkit member references were also checked against Spigot 26.2. These
checks do **not** replace real-server testing. In-game compatibility across the
full version range and Folia is not yet verified.

Before release, test startup, commands, menus, editor sync, reload, player logout,
and restart persistence on 1.8.8, pre/post-1.13 servers, and current Spigot/Paper/Folia.
On Folia, also test simultaneous overlapping claim creation/resizing from separate
regions, boundary protection, border visualization across regions, and claim teleports.

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

## Shared editor library

The web editor uses [BetterSync](https://github.com/RonanPlugins/BetterSync),
downloaded by Maven from JitPack as `com.github.RonanPlugins:BetterSync:1.1.0`.
No local BetterSync project or manual Maven install is required. Release `1.1.0` is published on JitPack. Update `bettersync.version` in the plugin and editor-test
POMs together when upgrading. Shade bundles and relocates the library into the
plugin JAR; server owners do not install a separate BetterSync plugin.

The plugin retains its scheduler, permissions, reload callbacks, and filtered
`editor.website` setting. Editor transport and configuration validation are
maintained in BetterSync.

### Editor schema ownership

Each plugin module owns `src/main/resources/META-INF/editor-schema.json`. Edit rules here,
not in the website or BetterSync's deprecated legacy profiles. `EditorSchema.load` reads
this resource from the plugin JAR; the client sends its independent `schemaVersion` and
checks the version returned by the website before applying changes.

Publish the same schema bytes as a public BetterSync GitHub release asset, then add the
asset URL and SHA-256 to the website's `editor-schemas.lock.json`. Keep old versions in the
lock for older servers. A changed rule requires a new schema version; plugin bug fixes
that do not affect configuration can retain the version. Schema publication does not
publish the rest of a private plugin repository.

Deploy the matching website and its schema-version database migrations before installing
the new plugin JARs. Schema resources are bundled automatically by Maven; no sibling
plugin/library checkout is needed to compile this project.
