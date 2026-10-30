"""Extracts the useful lines from a mixin-audit game log: ParCool mixin failures and crash causes."""
import re
import sys

PREFIX = re.compile(r"^\[[0-9:]+\] \[[^\]]*\] (\([^)]*\) )?")
KEEP = re.compile(
    r"PARCOOL_MIXIN_AUDIT|Mixin apply for mod parcool|parcool-common\.mixins\.json|Expected|but found|"
    r"Critical injection|No refMap|could not find|Could not find|Unable to locate|InvalidInjection|"
    r"InvalidMixin|InvalidAccessor|Caused by|Exception in thread|crash report|FATAL|NoSuchMethod|"
    r"NoSuchField|ClassNotFound|NoClassDefFound|at (\S+//)?com\.alrex|BootstrapMethodError")
DROP = re.compile(r"DEBUG|Mixing |Renaming|Selecting|Preparing|^\s*/\*\s+(org\.spongepowered|net\.fabricmc|java\.base|knot//)")

for path in sys.argv[1:]:
    print(f"##### {path}")
    seen = set()
    try:
        lines = open(path, encoding="utf-8", errors="replace").read().splitlines()
    except FileNotFoundError:
        print("(missing)")
        continue
    for line in lines:
        if not KEEP.search(line) or DROP.search(line):
            continue
        line = PREFIX.sub("", line)
        line = re.sub(r"\s{2,}", " ", line.strip(" /*")).strip()
        if line and line not in seen:
            seen.add(line)
            print(line)
