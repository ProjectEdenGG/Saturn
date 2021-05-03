#!/bin/bash
cd /home/minecraft/git/Saturn/

rm BearNationResourcePack.zip
rm /srv/http/cdn.bnn.gg/BearNationResourcePack.zip
git reset --hard origin/main
git pull
zip -r BearNationResourcePack.zip . -x '*.git*' -x 'deploy.sh' -x 'update.bat' -q
mv BearNationResourcePack.zip /srv/http/cdn.bnn.gg
chown www-data:www-data /srv/http/cdn.bnn.gg -R

