#!/usr/bin/env python3
"""Mechanical Minecraft 1.21.1 -> 26.3 rewrites of ParCool's sources (run after
neoforge_to_fabric.py). Only patterns that are safe to apply textually; the rest is ported by hand.

GUI (26.x renders by "extracting" render state):
  * GuiGraphics -> GuiGraphicsExtractor, and its drawing methods to their new names
  * overrides render/renderWidget/renderBackground -> extractRenderState / extractWidgetRenderState
    (extractContents for buttons) / extractBackground, and the matching calls
  * 2D pose stack: pushPose/popPose -> pushMatrix/popMatrix, translate/scale lose the z argument
  * no-op RenderSystem state calls (white shader colour, blend toggles) are removed
Input (26.x passes event records):
  * mouseClicked/mouseReleased/mouseDragged/keyPressed/keyReleased/charTyped/onClick/onRelease/
    onDrag overrides take the event; the old parameters are re-declared from it at the top of the
    method, so method bodies stay unchanged. Calls with the old argument lists construct the event.
Other renames: Minecraft#setScreen -> Minecraft.gui#setScreen, Entity#isInFluidType -> isInLiquid,
  Level#getMaxBuildHeight -> getMaxY.

Usage: python3 porting/mc_26_3.py
"""
import pathlib
import re

ROOT = pathlib.Path(__file__).resolve().parent.parent / 'src/main/java'

GUI_IMPORTS = {
    'import net.minecraft.client.gui.GuiGraphics;': 'import net.minecraft.client.gui.GuiGraphicsExtractor;',
}
EVENT_IMPORTS = [
    'import net.minecraft.client.input.MouseButtonEvent;',
    'import net.minecraft.client.input.MouseButtonInfo;',
    'import net.minecraft.client.input.KeyEvent;',
    'import net.minecraft.client.input.CharacterEvent;',
]
GRAPHICS_METHODS = {
    'drawString': 'text', 'drawCenteredString': 'centeredText', 'drawWordWrap': 'textWithWordWrap',
    'renderItem': 'item', 'renderFakeItem': 'fakeItem', 'renderItemDecorations': 'itemDecorations',
    'renderTooltip': 'setTooltipForNextFrame', 'renderComponentTooltip': 'setComponentTooltipForNextFrame',
}
BUTTON_BASES = {'AbstractButton', 'Button', 'AbstractSpriteButton', 'ImageButton'}


def split_args(s, start):
    """Parses the argument list starting after '(' at index start. Returns (args, end_index).

    Brackets are tracked by depth. '<' only counts as a generic bracket when it is directly followed
    by a type name or '?' (as in `new ArrayList<Foo>()`), and '>' only closes such a bracket, so
    comparisons such as `a > b` do not confuse the split.
    """
    depth, angle, cur, args, i, in_str = 0, 0, '', [], start, None
    while i < len(s):
        c = s[i]
        if in_str:
            cur += c
            if c == '\\':
                cur += s[i + 1]
                i += 1
            elif c == in_str:
                in_str = None
        elif c in '"\'':
            in_str = c
            cur += c
        elif c in '([{':
            depth += 1
            cur += c
        elif c in ')]}':
            if depth == 0 and c == ')':
                if cur.strip():
                    args.append(cur.strip())
                return args, i
            depth -= 1
            cur += c
        elif c == '<' and re.match(r'[A-Z?>]', s[i + 1:i + 2] or ' '):
            angle += 1
            cur += c
        elif c == '>' and angle > 0:
            angle -= 1
            cur += c
        elif c == ',' and depth == 0 and angle == 0:
            args.append(cur.strip())
            cur = ''
        else:
            cur += c
        i += 1
    raise ValueError('unbalanced')


def rewrite_calls(src, name, fn, min_args=None):
    """Rewrites `.name(args)` calls (and `name(args)` after `super.`) whose args satisfy fn."""
    out, pos = [], 0
    for m in re.finditer(r'(\b[\w.()]*?\.)?\b' + name + r'\(', src):
        if m.start() < pos:
            continue
        # Skip declarations: preceded by a type and followed by a body/throws.
        line_start = src.rfind('\n', 0, m.start()) + 1
        prefix = src[line_start:m.start()]
        if re.search(r'\b(public|protected|private|boolean|void|int)\s*$', prefix):
            continue
        try:
            args, end = split_args(src, m.end())
        except ValueError:
            continue
        new = fn(args, m.group(1) or '')
        if new is None:
            continue
        out.append(src[pos:m.start()])
        out.append(new)
        pos = end + 1
    out.append(src[pos:])
    return ''.join(out)


def method_decls(src, name):
    """Yields (match, params) for declarations of `name`."""
    pattern = re.compile(r'((?:@\w+\s+)*)((?:public|protected)\s+)(boolean|void)\s+' + name + r'\s*\(([^)]*)\)\s*(\{)')
    return list(pattern.finditer(src))


def params_of(text):
    return [p.strip().split()[-1] for p in text.split(',') if p.strip()]


def types_of(text):
    return [' '.join(p.strip().split()[:-1]).replace('final ', '').replace('@Nonnull ', '') for p in text.split(',') if p.strip()]


def convert_decl(src, name, old_types, new_params, prologue):
    for m in reversed(method_decls(src, name)):
        if types_of(m.group(4)) != old_types:
            continue
        names = params_of(m.group(4))
        lines = prologue(names)
        indent = re.match(r'\s*', src[src.rfind('\n', 0, m.start()) + 1:]).group(0) + '    '
        body = ''.join(f'\n{indent}{l}' for l in lines)
        decl = f'{m.group(1)}{m.group(2)}{m.group(3)} {name}({new_params}) {{{body}'
        src = src[:m.start()] + decl + src[m.end():]
    return src


def convert_input(src):
    src = convert_decl(src, 'mouseClicked', ['double', 'double', 'int'], 'MouseButtonEvent event, boolean doubleClick',
                       lambda n: [f'double {n[0]} = event.x();', f'double {n[1]} = event.y();', f'int {n[2]} = event.button();'])
    src = convert_decl(src, 'mouseReleased', ['double', 'double', 'int'], 'MouseButtonEvent event',
                       lambda n: [f'double {n[0]} = event.x();', f'double {n[1]} = event.y();', f'int {n[2]} = event.button();'])
    src = convert_decl(src, 'mouseDragged', ['double', 'double', 'int', 'double', 'double'], 'MouseButtonEvent event, double dragX, double dragY',
                       lambda n: [f'double {n[0]} = event.x();', f'double {n[1]} = event.y();', f'int {n[2]} = event.button();',
                                  f'double {n[3]} = dragX;', f'double {n[4]} = dragY;'])
    for key_method in ('keyPressed', 'keyReleased'):
        src = convert_decl(src, key_method, ['int', 'int', 'int'], 'KeyEvent event',
                           lambda n: [f'int {n[0]} = event.key();', f'int {n[1]} = event.keycode();', f'int {n[2]} = event.modifiers();'])
    src = convert_decl(src, 'charTyped', ['char', 'int'], 'CharacterEvent event',
                       lambda n: [f'char {n[0]} = (char) event.codepoint();', f'int {n[1]} = 0;'])
    src = convert_decl(src, 'onClick', ['double', 'double'], 'MouseButtonEvent event, boolean doubleClick',
                       lambda n: [f'double {n[0]} = event.x();', f'double {n[1]} = event.y();'])
    src = convert_decl(src, 'onRelease', ['double', 'double'], 'MouseButtonEvent event',
                       lambda n: [f'double {n[0]} = event.x();', f'double {n[1]} = event.y();'])
    src = convert_decl(src, 'onDrag', ['double', 'double', 'double', 'double'], 'MouseButtonEvent event, double dragX, double dragY',
                       lambda n: [f'double {n[0]} = event.x();', f'double {n[1]} = event.y();', f'double {n[2]} = dragX;', f'double {n[3]} = dragY;'])

    def mouse_event(a):
        return f'new MouseButtonEvent({a[0]}, {a[1]}, new MouseButtonInfo({a[2]}, 0))'

    src = rewrite_calls(src, 'mouseClicked', lambda a, r: f'{r}mouseClicked({mouse_event(a)}, false)' if len(a) == 3 else None)
    src = rewrite_calls(src, 'mouseReleased', lambda a, r: f'{r}mouseReleased({mouse_event(a)})' if len(a) == 3 else None)
    src = rewrite_calls(src, 'mouseDragged', lambda a, r: f'{r}mouseDragged({mouse_event(a)}, {a[3]}, {a[4]})' if len(a) == 5 else None)
    for key_method in ('keyPressed', 'keyReleased'):
        src = rewrite_calls(src, key_method, lambda a, r, k=key_method: f'{r}{k}(new KeyEvent({a[0]}, {a[1]}, {a[2]}))' if len(a) == 3 else None)
    src = rewrite_calls(src, 'charTyped', lambda a, r: f'{r}charTyped(new CharacterEvent({a[0]}))' if len(a) == 2 else None)
    return src


def class_bases(files):
    bases = {}
    for f in files:
        for m in re.finditer(r'class (\w+)(?:<[^>{]*>)?\s+extends\s+([\w.]+)', f.read_text(encoding='utf-8')):
            bases[m.group(1)] = m.group(2).rsplit('.', 1)[-1]
    return bases


def is_button(cls, bases):
    seen = set()
    while cls in bases and cls not in seen:
        seen.add(cls)
        cls = bases[cls]
        if cls in BUTTON_BASES:
            return True
    return cls in BUTTON_BASES


def convert_gui(src, bases):
    for old, new in GUI_IMPORTS.items():
        src = src.replace(old, new)
    src = re.sub(r'\bGuiGraphics\b', 'GuiGraphicsExtractor', src)
    for old, new in GRAPHICS_METHODS.items():
        src = re.sub(r'(\b(?:graphics|guiGraphics|g|gui|context|pGuiGraphics)\.)' + old + r'\(', r'\1' + new + '(', src)

    # renderWidget -> extractContents in buttons, extractWidgetRenderState elsewhere (per class body).
    def widget_method(m):
        cls = None
        for c in re.finditer(r'class (\w+)', src[:m.start()]):
            cls = c.group(1)
        return ('extractContents' if cls and is_button(cls, bases) else 'extractWidgetRenderState') + '('
    src = re.sub(r'(?<=void )renderWidget\(', widget_method, src)
    src = re.sub(r'(?<=super\.)renderWidget\(', widget_method, src)
    src = re.sub(r'\bvoid render\((\s*(?:@Nonnull\s+)?(?:final\s+)?GuiGraphicsExtractor\b)', r'void extractRenderState(\1', src)
    src = re.sub(r'\.render\((\s*(?:graphics|guiGraphics|pGuiGraphics|g)\s*,)', r'.extractRenderState(\1', src)
    for old, new in [('renderBackground', 'extractBackground'), ('renderTransparentBackground', 'extractTransparentBackground'),
                     ('renderBlurredBackground', 'extractBlurredBackground'), ('renderMenuBackground', 'extractMenuBackground')]:
        src = re.sub(r'\b' + old + r'\(', new + '(', src)

    # 2D pose stack (Matrix3x2fStack) in GUI code.
    src = src.replace('.pushPose()', '.pushMatrix()').replace('.popPose()', '.popMatrix()')

    src = rewrite_calls(src, 'translate', lambda a, r: f'{r}translate((float) ({a[0]}), (float) ({a[1]}))' if len(a) == 3 else None)
    src = rewrite_calls(src, 'scale', lambda a, r: f'{r}scale((float) ({a[0]}), (float) ({a[1]}))' if len(a) == 3 else None)

    src = re.sub(r'^[ \t]*RenderSystem\.setShaderColor\(\s*1(?:\.0)?f?\s*,\s*1(?:\.0)?f?\s*,\s*1(?:\.0)?f?\s*,\s*1(?:\.0)?f?\s*\);[ \t]*\n', '', src, flags=re.M)
    src = re.sub(r'^[ \t]*RenderSystem\.(enableBlend|disableBlend|defaultBlendFunc|enableDepthTest|disableDepthTest)\(\);[ \t]*\n', '', src, flags=re.M)
    return src


def convert_common(src):
    src = re.sub(r'(Minecraft\.getInstance\(\)|\bminecraft|\bmc)\.setScreen\(', r'\1.gui.setScreen(', src)
    src = re.sub(r'\.isInFluidType\(\)', '.isInLiquid()', src)
    src = src.replace('.getMaxBuildHeight()', '.getMaxY()').replace('.getMinBuildHeight()', '.getMinY()')
    return src


def add_imports(src, imports):
    needed = [i for i in imports if i not in src and re.search(r'\b' + i.rsplit('.', 1)[1][:-1] + r'\b', src.split('\n', 1)[1] if '\n' in src else '')]
    if not needed:
        return src
    m = list(re.finditer(r'^import [^\n]+\n', src, re.M))
    at = m[-1].end() if m else src.index('\n') + 1
    return src[:at] + ''.join(i + '\n' for i in needed) + src[at:]


def main():
    files = sorted(ROOT.rglob('*.java'))
    bases = class_bases(files)
    changed = 0
    for f in files:
        src = f.read_text(encoding='utf-8')
        new = convert_common(src)
        if 'GuiGraphics' in new or 'net.minecraft.client.gui' in new:
            new = convert_gui(new, bases)
            new = convert_input(new)
            new = add_imports(new, EVENT_IMPORTS)
        if new != src:
            f.write_text(new, encoding='utf-8')
            changed += 1
    print(f'{changed} files changed')


if __name__ == '__main__':
    main()
