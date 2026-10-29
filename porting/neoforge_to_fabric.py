#!/usr/bin/env python3
"""Mechanical NeoForge -> Fabric rewrite of ParCool's sources.

Only does what is safe to do textually; everything else is ported by hand. It is kept in the
repository so the same rewrite can be applied again when an upstream 4.0.x release is merged.

What it does, per .java file under src/main/java:
  * @OnlyIn(Dist.X)            -> @Environment(EnvType.X)   (same meaning: stripped on the other
                                                           physical side)
  * @SubscribeEvent lines       -> removed; Fabric has no annotation bus, events are wired in
                                   com.alrex.parcool.fabric.FabricEventBridge instead
  * NeoForge.EVENT_BUS.post(... -> ParCoolEventBus.post(...  (ParCool's own add-on API events)
  * ResourceLocation            -> Identifier               (renamed by Mojang in 1.21.11)
  * imports of NeoForge classes that have a same-named adapter in com.alrex.parcool.fabric.compat
    are pointed at the adapter (see IMPORTS)

Usage: python3 porting/neoforge_to_fabric.py [--check]
  --check  only list the NeoForge imports that are left (exit code 1 if any)
"""
import pathlib
import re
import sys

ROOT = pathlib.Path(__file__).resolve().parent.parent / 'src/main/java'
COMPAT = 'com.alrex.parcool.fabric.compat'

# NeoForge class -> replacement import. None removes the import (the code no longer needs it).
IMPORTS = {
    'net.neoforged.api.distmarker.OnlyIn': 'net.fabricmc.api.Environment',
    'net.neoforged.api.distmarker.Dist': 'net.fabricmc.api.EnvType',
    'net.neoforged.bus.api.SubscribeEvent': None,
    'net.neoforged.bus.api.EventPriority': None,
    'net.neoforged.bus.api.Event': f'{COMPAT}.event.Event',
    'net.neoforged.bus.api.ICancellableEvent': f'{COMPAT}.event.ICancellableEvent',
    'net.neoforged.bus.api.IEventBus': f'{COMPAT}.event.IEventBus',
    'net.neoforged.fml.event.IModBusEvent': None,
    'net.neoforged.fml.LogicalSide': f'{COMPAT}.network.LogicalSide',
    'net.neoforged.neoforge.common.NeoForge': f'{COMPAT}.event.ParCoolEventBus',
    'net.neoforged.neoforge.registries.DeferredRegister': f'{COMPAT}.registry.DeferredRegister',
    'net.neoforged.neoforge.registries.DeferredHolder': f'{COMPAT}.registry.DeferredHolder',
    'net.neoforged.neoforge.network.handling.IPayloadContext': f'{COMPAT}.network.IPayloadContext',
    'net.neoforged.neoforge.network.handling.DirectionalPayloadHandler': f'{COMPAT}.network.DirectionalPayloadHandler',
    'net.neoforged.neoforge.network.PacketDistributor': f'{COMPAT}.network.PacketDistributor',
    'net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent': f'{COMPAT}.network.RegisterPayloadHandlersEvent',
    'net.minecraft.resources.ResourceLocation': 'net.minecraft.resources.Identifier',
    # Removed from Minecraft in 26.x; same-shaped replacement.
    'net.minecraft.util.Tuple': 'com.alrex.parcool.util.Tuple',
}
# Game and registration events: adapters with the same simple name in COMPAT.event.
for name in [
    'neoforge.client.event.InputEvent', 'neoforge.client.event.ClientTickEvent',
    'neoforge.client.event.RegisterClientReloadListenersEvent', 'neoforge.client.event.ViewportEvent',
    'neoforge.client.event.RenderLevelStageEvent', 'neoforge.client.event.RenderGuiEvent',
    'neoforge.client.event.RenderFrameEvent', 'neoforge.client.event.RegisterKeyMappingsEvent',
    'neoforge.client.event.RegisterGuiLayersEvent', 'neoforge.client.event.MovementInputUpdateEvent',
    'neoforge.client.event.ModelEvent', 'neoforge.client.event.EntityRenderersEvent',
    'neoforge.client.event.ComputeFovModifierEvent',
    'neoforge.event.entity.living.LivingFallEvent', 'neoforge.event.entity.living.LivingEvent',
    'neoforge.event.entity.living.LivingDamageEvent', 'neoforge.event.tick.PlayerTickEvent',
    'neoforge.event.tick.LevelTickEvent', 'neoforge.event.entity.player.PlayerEvent',
    'neoforge.event.entity.EntityEvent', 'neoforge.event.entity.EntityAttributeModificationEvent',
    'neoforge.event.RegisterCommandsEvent', 'neoforge.event.AddReloadListenerEvent',
]:
    IMPORTS['net.neoforged.' + name] = f'{COMPAT}.event.{name.rsplit(".", 1)[1]}'

# Provided unchanged by Forge Config API Port on Fabric.
PROVIDED = ('net.neoforged.neoforge.common.ModConfigSpec', 'net.neoforged.fml.config.ModConfig')

IMPORT_RE = re.compile(r'^import (static )?([\w.]+);\n', re.M)


def convert(src: str) -> str:
    def fix_import(m):
        static, name = m.group(1) or '', m.group(2)
        # Static imports and nested classes: match the longest known class prefix.
        for old, new in IMPORTS.items():
            if name == old or name.startswith(old + '.'):
                return '' if new is None else f'import {static}{new}{name[len(old):]};\n'
        return m.group(0)

    src = IMPORT_RE.sub(fix_import, src)
    src = re.sub(r'@OnlyIn\(\s*(?:value\s*=\s*)?Dist\.CLIENT\s*\)', '@Environment(EnvType.CLIENT)', src)
    src = re.sub(r'@OnlyIn\(\s*(?:value\s*=\s*)?Dist\.DEDICATED_SERVER\s*\)', '@Environment(EnvType.SERVER)', src)
    src = re.sub(r'^[ \t]*@SubscribeEvent(\([^)]*\))?[ \t]*\n', '', src, flags=re.M)
    src = src.replace('NeoForge.EVENT_BUS.post(', 'ParCoolEventBus.post(')
    # 26.x made these Level fields private/protected; the accessor methods exist on every version.
    src = re.sub(r'\.isClientSide\b(?!\s*\()', '.isClientSide()', src)
    src = re.sub(r'\b(level|level\(\)|world|getLevel\(\)|clientLevel)\.random\b', r'\1.getRandom()', src)
    src = re.sub(r'\bResourceLocation\b', 'Identifier', src)
    src = re.sub(r'\s+implements IModBusEvent\b', '', src)
    src = re.sub(r',\s*IModBusEvent\b', '', src)
    # The distmarker imports are only needed where the annotations are still used.
    if '@Environment(' not in src:
        src = re.sub(r'^import net\.fabricmc\.api\.(Environment|EnvType);\n', '', src, flags=re.M)
    elif 'import net.fabricmc.api.EnvType;' not in src:
        src = src.replace('import net.fabricmc.api.Environment;', 'import net.fabricmc.api.EnvType;\nimport net.fabricmc.api.Environment;')
    return src


def main():
    check = '--check' in sys.argv
    left, changed = {}, 0
    for f in sorted(ROOT.rglob('*.java')):
        src = f.read_text(encoding='utf-8')
        if not check:
            new = convert(src)
            if new != src:
                f.write_text(new, encoding='utf-8')
                changed += 1
                src = new
        for name in re.findall(r'^import (?:static )?(net\.neoforged\.[\w.]+);', src, re.M):
            if not name.startswith(PROVIDED):
                left.setdefault(name, []).append(str(f.relative_to(ROOT)))
    if not check:
        print(f'{changed} files changed')
    for name, files in sorted(left.items()):
        print(f'{name}: {len(files)}  ({", ".join(p.rsplit("/", 1)[1] for p in files[:4])})')
    sys.exit(1 if check and left else 0)


if __name__ == '__main__':
    main()
