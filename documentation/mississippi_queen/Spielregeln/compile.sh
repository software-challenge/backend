#/bin/sh
while true; do
  inotifywait -e close_write,moved_to,create . |
  while read -r directory events filename; do
    if [ "$filename" = "spielregeln.tex" ]; then
      xelatex spielregeln.tex
    fi
  done
done
