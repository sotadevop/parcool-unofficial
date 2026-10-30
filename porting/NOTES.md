# ParCool 4.0 -> Fabric 26.3: porting notes

Base: upstream `1.21.1-v4` (4.0.1.5, commit 81136ce7). Fabric Loom 1.19 (no remapping), Fabric API
0.165.0+26.3, Forge Config API Port 26.3.1 (provides `net.neoforged.neoforge.common.ModConfigSpec`
and `net.neoforged.fml.config.ModConfig` unchanged; registration via
`fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry`).

## Approach

`porting/neoforge_to_fabric.py` does the textual part (imports, `@OnlyIn`, `@SubscribeEvent`,
`ResourceLocation` -> `Identifier`). NeoForge types that upstream code uses as parameters keep their
simple names and live in `com.alrex.parcool.fabric.compat` (minimal adapters, only the members
upstream calls). That keeps the diff to upstream small, so later 4.0.x releases merge cleanly.

## Where each NeoForge event comes from on Fabric

| Adapter event | Source |
|---|---|
| ClientTickEvent.Pre/Post | ClientTickEvents.START/END_CLIENT_TICK |
| PlayerTickEvent.Post | mixin Player#tick TAIL (both sides) |
| LevelTickEvent.Post | ServerTickEvents / ClientTickEvents END_LEVEL_TICK |
| PlayerEvent.Clone | ServerPlayerEvents.COPY_FROM (alive -> !isWasDeath) |
| PlayerEvent.PlayerChangedDimensionEvent | ServerEntityLevelChangeEvents.AFTER_PLAYER_CHANGE_LEVEL |
| LivingFallEvent | mixin LivingEntity#causeFallDamage HEAD |
| LivingEvent.LivingJumpEvent | mixin LivingEntity#jumpFromGround TAIL |
| LivingEvent.LivingVisibilityEvent | mixin LivingEntity#getVisibilityPercent RETURN |
| LivingDamageEvent.Pre | mixin LivingEntity#actuallyHurt (damage argument) |
| EntityEvent.Size | mixin Entity#refreshDimensions (getDimensions result) |
| InputEvent.InteractionKeyMappingTriggered | mixin Minecraft#startAttack / #startUseItem HEAD |
| MovementInputUpdateEvent | mixin LocalPlayer#aiStep after input tick |
| ViewportEvent.ComputeCameraAngles | mixin Camera#update (as in the 3.4 port) |
| ComputeFovModifierEvent | mixin AbstractClientPlayer#getFieldOfViewModifier RETURN |
| RenderFrameEvent.Pre | mixin GameRenderer#extract HEAD (as in the 3.4 port) |
| RenderGuiEvent.Post | HudElementRegistry.addLast |
| registration events | fired once from ParCool / ParCoolClient init |

## Deliberate differences

- Mod integrations (Curios, Better Third Person, Shoulder Surfing, Epic Fight, Paraglider) are not
  ported; `extern/` keeps "not installed" stubs so call sites are unchanged.
- Server only accepts client packets about the sending player (upstream applied them to any UUID).
- NeoForge payload version negotiation (`registrar(connectionId)`) has no Fabric equivalent.
- Brewing recipes are data files (26.3 made brewing data-driven).
- Item colours and the grappling hook's item renderer are client item definitions
  (`assets/parcool/items`), as 26.x requires for every item.
- Zipline ropes: each segment is drawn once (upstream drew it twice in an inner loop).
- Stamina HUD height: registered with HudStatusBarHeightRegistry, so it stacks with the air bar.

## Open

- Traceur gloves arm pose (upstream: IClientItemExtensions#getArmPose) -> 26.3 render state.
- Grapple rope / target overlay level rendering (upstream: RenderLevelStageEvent) -> LevelRenderEvents.
