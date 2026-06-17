@echo off
REM MS Simulator - GitHub Pages Deployment Script
REM Builds web distribution and copies to docs/

echo Building MS Simulator web distribution...
call ./gradlew.bat composeApp:wasmJsBrowserDistribution
if %ERRORLEVEL% neq 0 (
    echo Build failed!
    exit /b 1
)

echo Copying to docs/...
if exist docs rmdir /s /q docs
mkdir docs
xcopy /E /I /Y "composeApp\build\dist\wasmJs\productionExecutable\*" "docs\"

echo.
echo Done! Files in docs/:
dir docs\*.* /B

echo.
echo Next steps:
echo 1. git add docs/ && git commit -m "Deploy to GitHub Pages"
echo 2. git push
echo 3. GitHub: Settings ^> Pages ^> Source: "Deploy from branch" ^> branch: main, folder: /docs
echo.
echo Data source: https://github.com/r00tIsRoot/MSSimulatorData
echo Make sure skills.json and bosses.json are pushed there.
