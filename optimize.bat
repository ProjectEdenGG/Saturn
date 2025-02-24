@echo off
setlocal enabledelayedexpansion

echo [INFO] Starting optimization process...

REM --all Support
if "%1"=="--all" (
    echo [INFO] Running on all files recursively...
    for /r %%i in (*.png) do (
        set /a index+=1
        echo [DEBUG] File index !index!: %%~i
        call :process_file "%%~i"
    )
    goto :done
)

REM Temporary file for storing file list
set "tempFile=%TEMP%\filelist.txt"
del "%tempFile%" >nul 2>&1

REM Generate the file list
(
    git diff HEAD --name-only --diff-filter=ACMR
) > "%tempFile%"

echo [INFO] Temporary file content:
type "%tempFile%"

set "index=0"

REM Iterate through each file in the list
for /f "usebackq delims=" %%i in ("%tempFile%") do (
    set /a index+=1
    set "currentFile=%%i"
    call :process_file "%%i"
)

REM Cleanup
del "%tempFile%" >nul 2>&1
echo [INFO] Optimization process completed!
exit /b

REM Subroutine for file processing
:process_file
setlocal
set "filePath=%~1"

REM Check if the file exists
if not exist "!filePath!" (
    echo [WARN] File not found: "!filePath!"
    endlocal
    goto :eof
)

REM Skip non-PNG files
if /i not "!filePath:~-4!"==".png" (
    echo [INFO] Skipping non-PNG file: "!filePath!"
    endlocal
    goto :eof
)

REM Optimize PNG files
echo [INFO] Running optipng on: "!filePath!"
optipng -o3 "!filePath!" >nul 2>&1
if errorlevel 1 echo [ERROR] optipng failed for "!filePath!"

echo [INFO] Running zopflipng on "!filePath!"
call zopflipng --iterations=15 --filters=0me --lossy_transparent -ym "!filePath!" "!filePath!" >nul 2>&1
if errorlevel 1 echo [ERROR] zopflipng failed for !filePath!

endlocal
goto :eof