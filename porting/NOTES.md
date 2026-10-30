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

## Status (2026-10-30, end of day)

- Compiles; mixin audit passes on a dedicated server and a client. Waiting for the in-game test.
- All actions available from the start: `enable_skill_tree` defaults to false (server config).
- CI scans shared classes for lambdas that touch client classes (`.github/scripts/client-refs.py`).
  The 14 it lists are checked: only PoleClimb's was a real server crash (fixed); the rest are in
  client-only classes or do not make the verifier load a client class.

## Known differences to upstream 4.0

- Guide book recipe and brewing boxes are filled in singleplayer only (since 1.21.2 the client does
  not receive recipes; the guide reads the integrated server's).
- No enchantment glint on the equipment model.
- Mod integrations (Curios, Epic Fight, ...) are stubs.

## 26.3 facts already looked up (no need to probe again)

- Gone: MultiBufferSource, ItemInHandRenderer, ItemRenderer, LightTexture, TextureAtlasHolder,
  LayeredDraw, Equipable, SimpleCraftingRecipeSerializer, PotionBrewing class, block codecs.
- Replacements: SubmitNodeCollector (+ OrderedSubmitNodeCollector), FirstPersonHandsAndItemsRenderer,
  ItemModelResolver/ItemStackRenderState, LightCoordsUtil, AtlasManager/SpriteGetter
  (client/resources/model/sprite), Fabric LevelRenderEvents (+ LevelRenderContext),
  HudStatusBarHeightRegistry, ArgumentTypeRegistry, Equippable via Item.Properties#equippable.
- Player model: client/model/player/PlayerModel; renderer AvatarRenderer with AvatarRenderState
  (extends HumanoidRenderState extends ArmedEntityRenderState). HumanoidModel<T extends HumanoidRenderState>.
- Camera: entity(), position(), forwardVector()/upVector()/leftVector() return Vector3fc.
- Blocks: neighborChanged(BlockState, Level, BlockPos, Block, Orientation, boolean);
  updateShape(BlockState, LevelReader, ScheduledTickAccess, BlockPos, Direction, BlockPos, BlockState, RandomSource);
  removal side effects in BlockEntity#preRemoveSideEffects; PushReaction.POPPED (DESTROY is gone).
- Block entity data: saveAdditional(ValueOutput) / loadAdditional(ValueInput); keys kept as in 4.0.
- Input: ClientInput#keyPresses is the immutable record Input(forward, backward, left, right, jump, shift, sprint).
- GUI: GuiGraphicsExtractor#enableScissor/disableScissor; RenderSystem#setShaderColor is gone.
- Dyes: DataComponents.DYE on the stack; DyedItemColor(int).

## Deliberate fixes to upstream bugs

- Dye recipe: upstream summed unmasked colour shifts, so green and blue overflowed; channels are masked.

## Open

- Traceur gloves arm pose (upstream: IClientItemExtensions#getArmPose) -> 26.3 render state.
- Grapple rope / target overlay level rendering (upstream: RenderLevelStageEvent) -> LevelRenderEvents.
