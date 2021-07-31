#!/bin/zsh
echo "optipng"
optipng -o3 **/*.png

echo "zopflipng"
for i in **/*.png; do
 [ -f "$i" ] || break
 zopflipng --iterations=15 --filters=0me --lossy_transparent -ym "$i" "$i"
done
echo "done"