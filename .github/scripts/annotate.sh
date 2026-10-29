#!/usr/bin/env bash
# Emits a text file as GitHub error annotations, so it can be read through the API without the log.
# Usage: annotate.sh <file> <first-chunk> <title>
# An annotation message holds at most 4096 characters and GitHub keeps at most 10 error annotations
# per step, so the file is cut into chunks of under 4000 characters and each call emits 10 of them.
set -euo pipefail
python3 - "$@" <<'PY'
import sys
path, first, title = sys.argv[1], int(sys.argv[2]), sys.argv[3]
chunks, cur = [], ""
for line in open(path, encoding="utf-8", errors="replace"):
    line = line.rstrip("\n")[:3900] + "\n"
    if len(cur) + len(line) > 3950:
        chunks.append(cur)
        cur = ""
    cur += line
if cur:
    chunks.append(cur)
for i in range(first, min(first + 10, len(chunks))):
    msg = chunks[i].replace("%", "%25").replace("\r", "").replace("\n", "%0A")
    print(f"::error title={title} {i:02d}::{msg}")
PY
