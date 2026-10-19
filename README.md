# ParCool! for Fabric 26.3 (unofficial port)

An unofficial port of **ParCool!**, the parkour mod by **alRex_U**, to **Minecraft 26.3** on **Fabric**.

ParCool adds parkour movement to Minecraft: grab ledges and pull yourself up, run along and up walls,
wall jump, crawl, slide, vault, roll, dodge, flip, ride ziplines and more.

> This is **not** the official ParCool. The original mod only exists for NeoForge/Forge up to
> Minecraft 1.21.11. Please report problems with this port **here**, not to alRex_U.

## Download

Get the jar from the [Releases](../../releases) page.

## Requirements

| | Version |
|---|---|
| Minecraft | 26.3 |
| Fabric Loader | 0.19.5 or newer |
| [Fabric API](https://modrinth.com/mod/fabric-api) | any 26.3 version |
| [Architectury API](https://modrinth.com/mod/architectury-api) | 22.0.0 or newer (26.3) |
| Java | 25 |

Optional: [Mod Menu](https://modrinth.com/mod/modmenu) adds a **Configure** button that opens
ParCool's settings.

## Installing

1. Install Fabric API and Architectury API for 26.3 (for example through your launcher's mod browser).
2. Put the ParCool jar from Releases into your `mods` folder.
3. If you use another crawl mod (such as *Crawl*), disable it: ParCool has its own crawl.

Back up your world before using it in a world you care about.

## Controls

All keys can be changed in **Options > Controls > Key Binds > ParCool**.

| Action | Default |
|---|---|
| Grab a ledge / hang | Jump at a wall, hold **right mouse** near the edge |
| Pull up | **Space** while hanging |
| Horizontal wall run | Sprint along a wall, jump, hold **R** |
| Run up a wall | Fast run (**Left Ctrl** while sprinting) straight at a wall at least 3 blocks high, look slightly up, jump and hold **Space** |
| Wall jump | Back or side to the wall, press **Space** in the air (blocked while facing the wall, and while Creative flight is active) |
| Crawl / slide | **C** (slide: press it during fast run) |
| Roll / breakfall | Hold **R** before landing |
| Dodge | **R** + A/D/S on the ground |
| Settings | **Alt + P** |
| Turn ParCool on/off | **Ctrl + P** |

Vaulting triggers when you sprint at a ~1 block obstacle with free space behind it. If it gets in
the way (for example on hills), the settings can make it require a key press.

## What is included

Everything from ParCool **3.4.3.3** (the version this port is based on), tested in game on 26.3:
all parkour actions, animations, stamina, the zipline, the energy drinks, the settings screen.

Not included: features that only exist in **ParCool 4.0** (grappling hook, skill tree, equipment,
new animation engine). 4.0 is a rewrite and has no Fabric version.

## Changes compared to the original

- Runs on Fabric (through Architectury API) instead of NeoForge. Fabric only; there is no NeoForge
  jar.
- Ported to Minecraft 26.3: new GUI rendering, SDL input, the new rendering pipeline, data-driven
  brewing (the energy drink recipes are data files now).
- Fixed: the key binding category was shown untranslated.
- Added: Mod Menu support.
- Server-side validation of client packets, from the Architectury port this is based on.

## Credits

- **alRex_U**: ParCool, its design and assets ([original project](https://github.com/alRex-U/ParCool),
  [CurseForge](https://www.curseforge.com/minecraft/mc-mods/parcool)).
- **sanufsoii**: the Architectury/Fabric port for 1.21.x this fork builds on
  ([parcool-unofficial](https://github.com/sanufsoiii/parcool-unofficial)).
- **sotadevop**: the port to Minecraft 26.3.

## License

GNU Lesser General Public License v3.0, the same as the original. The full text is in
[LICENSE](LICENSE). The complete source code of every release is in this repository.

## Building

Requires JDK 25. On a fresh checkout `:common` has to be built once first:

```bash
./gradlew :common:build
./gradlew build
# -> fabric/build/libs/parcool-<version>-fabric.jar
```

Every push is built by GitHub Actions (`.github/workflows/build.yml`). Besides compiling, CI starts a
dedicated server and a headless client and checks that every mixin applies to the Minecraft
version, so a changed Minecraft method fails CI instead of crashing a game. See
[BUILDING.md](BUILDING.md) for details.
