"""Condenses Gradle/javac output into one line per compiler error, plus Gradle's failure summary.

Each javac error spans several lines (message, source line, caret, symbol/location/required/found).
They are joined into a single line so the whole list fits into a few annotations.
"""
import re
import sys

ERR = re.compile(r"^\s*(\S+?\.java):(\d+): (error|warning): (.*)$")
DETAIL = re.compile(r"^\s*(symbol|location|required|found|reason|method|constructor)\b")

out, cur, code_taken = [], None, False
for raw in open(sys.argv[1], encoding="utf-8", errors="replace"):
    line = raw.rstrip("\n")
    m = ERR.match(line)
    if m:
        if cur:
            out.append(cur)
        path = m.group(1).split("/java/", 1)[-1]
        cur, code_taken = f"{path}:{m.group(2)}: {m.group(4)}", False
        if m.group(3) == "warning":
            cur = None
        continue
    if cur is None:
        continue
    if DETAIL.match(line):
        cur += " | " + line.strip()
    elif not code_taken and line.strip() and line.strip() != "^":
        cur += " || " + line.strip()
        code_taken = True
    elif line.strip() == "^" or set(line.strip()) == {"^"}:
        continue
    elif not line.startswith(" "):
        out.append(cur)
        cur = None
if cur:
    out.append(cur)

seen, errors = set(), []
for e in out:
    if e not in seen:
        seen.add(e)
        errors.append(e)
errors.sort()
print(f"{len(errors)} compiler errors")

# Grouped view first: one line per distinct problem (message plus the missing symbol / location
# type), most frequent first, with up to three example places. Most porting errors are renames, so
# this is usually enough to fix whole groups without reading every error.
def key(e):
    msg = e.split(": ", 1)[1].split(" || ")[0]
    details = [d.strip() for d in e.split(" | ")[1:] if d.strip().startswith(("symbol", "location: class", "location: interface", "location: variable"))]
    details = [d.split(" of type ")[-1] if d.startswith("location: variable") else d for d in details]
    return msg + ("  [" + "; ".join(details) + "]" if details else "")

groups = {}
for e in errors:
    groups.setdefault(key(e), []).append(e.split(": ", 1)[0].rsplit("/", 1)[-1])
print(f"--- {len(groups)} distinct problems ---")
for k, places in sorted(groups.items(), key=lambda kv: (-len(kv[1]), kv[0])):
    print(f"{len(places)}x {k}  @ {', '.join(places[:3])}")
print("--- all errors ---")
print("\n".join(errors))

text = open(sys.argv[1], encoding="utf-8", errors="replace").read()
m = re.search(r"\* What went wrong:\n(.*?)\n\* Try:", text, re.S)
if m:
    print("\n--- Gradle summary ---")
    print("\n".join(m.group(1).splitlines()[:60]))
