#!/bin/bash
cd /home/minecraft/git/Saturn/

rm ResourcePack.zip
rm /srv/http/cdn/ResourcePack.zip
git reset --hard origin/main
git pull
zip -r ResourcePack.zip 'assets/' 'pack.mcmeta' 'pack.png' -q
mv ResourcePack.zip /srv/http/cdn
chown www-data:www-data /srv/http/cdn -R
