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
print("\n".join(errors))

text = open(sys.argv[1], encoding="utf-8", errors="replace").read()
m = re.search(r"\* What went wrong:\n(.*?)\n\* Try:", text, re.S)
if m:
    print("\n--- Gradle summary ---")
    print("\n".join(m.group(1).splitlines()[:60]))
