# SpoutCraft Redux Server

A fork of [Purpur](https://github.com/PurpurMC/Purpur) for Minecraft 26.3 that adds the SpoutCraft Redux API. Players with the [SpoutCraft Redux client](https://github.com/SpoutCraftRedux/SpoutCraftRedux-Client) get the extra features. Everyone else plays as on a normal Purpur server.

The chain is Paper, then Purpur, then this fork, built with paperweight-patcher from Purpur's fork template. Our own code lives in new files under `spoutcraftredux-server/src/main/java` and `spoutcraftredux-api/src/main/java`. The patches only add small hooks into Minecraft, Paper and Purpur, one feature per patch, marked with `// SpoutCraft Redux` comments.

## What it does so far

- A configuration-phase handshake with the client. The server waits up to `handshake.timeout-ticks` (40 by default) for an answer, and skips the wait for clients that report the `vanilla` brand.
- `player.spoutCraftRedux()` on every `Player`, with the negotiated protocol, capabilities and mod version.
- `SpoutCraftReduxClientReadyEvent` and `SpoutCraftReduxClientMissingEvent`, both fired just before `PlayerJoinEvent`.
- `/spoutcraftredux list`, which shows who has the client.
- `spoutcraftredux.yml` in the server folder for settings.

## Getting the code

The protocol is a submodule, so clone with:

```
git clone --recurse-submodules https://github.com/SpoutCraftRedux/SpoutCraftRedux-Server.git
```

You need Java 25. paperweight applies the patches with Git, so the folder has to be a real clone, not a zip download.

## Building

```
./gradlew applyAllPatches
./gradlew createPaperclipJar
```

The server jar ends up in `spoutcraftredux-server/build/libs/`. Local builds are versioned `26.3.local-SNAPSHOT`. Set `BUILD_NUMBER=<n>` to get the CI version, `26.3.build.<n>-stable`.

## CI and publishing

`.github/workflows/build.yml` builds every push and pull request. A push to `main` also:

- publishes `spoutcraftredux-api` to https://repo.spoutcraftredux.org/snapshots as `26.3.build.<run number>-stable`
- uploads the paperclip jar to https://api.spoutcraftredux.org as build `<run number>` of project `spoutcraftredux-server`, version `26.3`

The workflow uses these repository secrets:

| Secret | Used for |
|---|---|
| `REPO_USERNAME`, `REPO_PASSWORD` | Reposilite token that can write to `releases` and `snapshots` |
| `DOWNLOADS_API_TOKEN` | Upload token for api.spoutcraftredux.org |
| `DOWNLOADS_API_TOKEN_STAGING` | Upload token for staging-api.spoutcraftredux.org (not used by CI yet) |

To publish the API by hand:

```
ORG_GRADLE_PROJECT_spoutcraftreduxUsername=<user> \
ORG_GRADLE_PROJECT_spoutcraftreduxPassword=<token> \
./gradlew :spoutcraftredux-api:publishAllPublicationsToSpoutcraftreduxRepository
```

## Running a dev server

```
./gradlew runDevServer
```

It runs from `run/`. For testing with the dev client, set `online-mode=false` and `white-list=false` in `run/server.properties`.

To load the test plugin, which logs the SpoutCraft Redux join events, create `test-plugin.settings.gradle.kts` containing `include(":test-plugin")`.

## Working on patches

Run `applyAllPatches` first. It sets up these source trees, each of which is a git repo of its own:

- `spoutcraftredux-server/src/minecraft/java` for Minecraft
- `paper-server` and `paper-api` for Paper and CraftBukkit
- `purpur-server` and `purpur-api` for Purpur

Edit there and commit inside that folder. Then run `./gradlew rebuildAllServerPatches` (or `rebuildPaperApiPatches` for API changes) and commit the changed `.patch` files here.

To move to a newer Purpur, change `purpurCommit` in `gradle.properties` and run `applyAllPatches`. `scripts/updateUpstream.sh` runs the whole update and commits the result.

## License

Our code is LGPL-3.0 ([LICENSE](LICENSE), [COPYING](COPYING)). The server includes Paper and Purpur, so a built server is distributed under the GPL-3.0 like its upstreams. The build files from Purpur's template are MIT.
