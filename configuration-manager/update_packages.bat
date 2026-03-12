@echo off
setlocal enabledelayedexpansion

set "SOURCE_DIR=C:\dev\WORK\atomic\spring\service-registry\src\main\java\com\angrysurfer\spring\atomic"
set "OLD_PKG=com.angrysurfer.atomic.service.registry"
set "NEW_PKG=com.angrysurfer.spring.atomic"

set "count=0"

for /r "%SOURCE_DIR%" %%f in (*.java) do (
    set "file=%%f"
    powershell -Command "(Get-Content '!file!' -Raw) -replace 'com\\.angrysurfer\\.atomic\\.service\\.registry', 'com.angrysurfer.spring.atomic' | Set-Content '!file!' -NoNewline"
    set /a count+=1
    echo Processed: !file!
)

echo.
echo Total files processed: %count%
