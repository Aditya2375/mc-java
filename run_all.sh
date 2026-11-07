#!/bin/sh
# Runs every study. Needs a JDK 11 or newer. Exits non-zero if any check fails.
status=0
for f in src/*.java; do
  echo "== $f"
  java "$f" || status=1
done
exit $status
