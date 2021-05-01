@echo off
cd %appdata%\.minecraft\resourcepacks

if "%1"=="--install" (
    goto :install
) else (
    goto :update
)

:install
if exist Saturn (
    echo.
    echo Saturn is already installed
    echo.
    goto :stop
)

echo.
echo Installing Saturn...
echo.
git clone https://github.com/WakkaFlocka239/Saturn
echo.
echo.
echo Installed Saturn in your Minecraft resource packs, enable it in the resource pack screen.
echo.
echo Update the resource pack by double clicking "update.bat" in '.minecraft\resourcepacks\Saturn'
echo.
echo You can also create a shortcut to the update.bat file and put it anywhere you like
echo.
echo You can now close this window
echo.
goto :stop


:update
if not exist Saturn (
    goto :install
)

echo.
echo Updating Saturn...
echo.
cd Saturn
git reset --hard
git pull
echo.
echo.
echo Saturn has been updated, reload your resource packs with F3+T to see the changes
echo.
goto :stop


:stop:
pause