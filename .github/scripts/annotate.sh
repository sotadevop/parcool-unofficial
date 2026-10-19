#!/usr/bin/env bash
# Emits a text file as GitHub error annotations, so it can be read through the API without the log.
# Usage: annotate.sh <file> <first-chunk> <title>
# GitHub keeps at most 10 error annotations per step, so each call emits chunks first..first+9.
set -euo pipefail
file="$1"; first="$2"; title="$3"
lines_per_chunk=120
total=$(wc -l < "$file")
for i in $(seq "$first" $((first + 9))); do
  start=$((i * lines_per_chunk + 1))
  [ "$start" -gt "$total" ] && break
  msg=$(sed -n "${start},$((start + lines_per_chunk - 1))p" "$file" | sed -e 's/%/%25/g' -e 's/\r//g' | awk '{printf "%s%%0A", $0}')
  echo "::error title=${title} $(printf '%02d' "$i")::${msg}"
done
