# Building ParCool (Fabric, Minecraft 26.3)

Requires JDK 25. All versions are in `gradle.properties` and `settings.gradle`:
Minecraft 26.3, Fabric Loader 0.20.0, Fabric API 0.163.0+26.3, Architectury API 22.0.5,
`dev.architectury.loom-no-remap` 1.17, Gradle 9.7.1.

| Module   | Contents |
|----------|----------|
| `common` | The whole mod, loader agnostic: registries (Architectury `DeferredRegister`), the `ParCoolData` player-data subsystem, the payload plumbing, the JSON config, the vanilla mixins and the `platform` seam. Must not import `net.fabricmc.*` (checked by `checkCommonLoaderIndependence`). |
| `fabric` | Fabric entrypoints, `FabricParCoolPlatform`, `FabricParCoolNetwork`, the Mod Menu integration and the distributable jar. |

Minecraft is unobfuscated since 26.1, so there is no mapping or remapping step: the distributable
jar (`:fabric:distJar`) merges the `common` and `fabric` jars as they are compiled.

## Building

```bash
./gradlew :common:build   # once on a fresh checkout
./gradlew build
# -> fabric/build/libs/parcool-<version>-fabric.jar
```

`:common` has to exist before the `fabric` module can be configured, which is why a fresh checkout
needs the first command.

## Running

```bash
./gradlew :fabric:runClient
./gradlew :fabric:runServer
```

`-PparcoolMixinAudit` makes either run load every class ParCool's mixins target, print
`PARCOOL_MIXIN_AUDIT_OK` and exit. CI uses this on a dedicated server and on a headless client.

## CI

- `.github/workflows/build.yml`: builds, checks the jar contents, runs the mixin audit, and on a
  published GitHub release (tag such as `mc26.3-port.1`) attaches the jar and sets the notes from
  `.github/release-notes/<tag>.md`.
- `.github/workflows/probe.yml`: a porting helper. Pushing a change to `.github/probe-request.env`
  prints signatures of Minecraft, Fabric and Architectury classes of the target version.

## Porting to a new Minecraft version

Raise the versions in `gradle.properties` and the Gradle wrapper, push, then fix the compile errors
and the mixin audit findings.
