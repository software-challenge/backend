#!/bin/bash

# Converts all SVG images to PDF versions which may be used in a LaTeX document. Uses inkscape for converstion.

for i in *.svg; do
  base=$(basename -s .svg $i)
  echo "converting $base.svg -> $base.pdf"
  inkscape -D -z --file=$base.svg --export-pdf=$base.pdf
done
