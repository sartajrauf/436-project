@echo off

REM README: Only run this script when initially setting up the vscode
REM environment for the first time. This script will download JavaFX,
REM JUnit, and ensure Java 21 is set up correctly in the VSCode environment.

if not "%java_version%"=="21" (
    echo WARNING Java 21 is required, but version %java_version% was detected.
    echo Please make sure you configure vscode to use Java 21. This can be
    echo done by editing the classpath using
    echo "View > Command Palette... > Java: Configure Classpath > JDK Runtime > JavaSE-21"
)

REM Create necessary directories
mkdir ".\lib"
mkdir ".\.vscode"

echo Downloading 'openjfx-21.0.1' (this may take a while, give it a minute)...

REM Download the file
powershell -command "(New-Object System.Net.WebClient).DownloadFile('https://download2.gluonhq.com/openjfx/21.0.1/openjfx-21.0.1_windows-x64_bin-sdk.zip', 'openjfx-21.0.1_windows-x64_bin-sdk.zip')"

echo Installing 'openjfx-21.0.1'

REM Extract the contents of the downloaded file
powershell -command "Expand-Archive -Path '.\openjfx-21.0.1_windows-x64_bin-sdk.zip' -DestinationPath '.\lib'"

REM Download Gson JAR
echo Downloading 'Gson 2.11.0'...
powershell -Command "Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/com/google/code/gson/gson/2.11.0/gson-2.11.0.jar' -OutFile 'lib\gson-2.11.0.jar' -Headers @{ 'User-Agent' = 'Mozilla/5.0' }"

echo Creating .vscode files

REM Create and populate the launch.json file
echo { > ".\.vscode\launch.json"
echo  "configurations": [ >> ".\.vscode\launch.json"
echo    { >> ".\.vscode\launch.json"
echo      "type": "java", >> ".\.vscode\launch.json"
echo      "name": "Debug (Launch) - GUI", >> ".\.vscode\launch.json"
echo      "request": "launch", >> ".\.vscode\launch.json"
echo      "mainClass": "${workspaceFolder}/src/gui/GUI.java", >> ".\.vscode\launch.json"
echo      "vmArgs": "--module-path ./lib/javafx-sdk-21.0.1/lib --add-modules javafx.controls,javafx.media,javafx.fxml" >> ".\.vscode\launch.json"
echo    } >> ".\.vscode\launch.json"
echo  ] >> ".\.vscode\launch.json"
echo } >> ".\.vscode\launch.json"

REM Create and populate the settings.json file
echo { > ".\.vscode\settings.json"
echo     "java.project.referencedLibraries": [ >> ".\.vscode\settings.json"
echo         "lib/**/*.jar" >> ".\.vscode\settings.json"
echo     ], >> ".\.vscode\settings.json"
echo     "java.project.sourcePaths": [ >> ".\.vscode\settings.json"
echo         "./src" >> ".\.vscode\settings.json"
echo     ] >> ".\.vscode\settings.json"
echo } >> ".\.vscode\settings.json"

REM Define the JUnit Platform Console Standalone dependency URL
set "junitConsoleVersion=1.11.1"
set "dependencyUrl=https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/%junitConsoleVersion%/junit-platform-console-standalone-%junitConsoleVersion%.jar"

REM Download the dependency
powershell -Command "Invoke-WebRequest -Uri '%dependencyUrl%' -OutFile 'lib\junit-platform-console-standalone-%junitConsoleVersion%.jar'"

REM Output a message indicating completion
echo JUnit Platform Console Standalone has been downloaded to lib\

REM Check if .classpath exists and rename it to .classpath.DISABLED
if exist ".classpath" (
    ren ".classpath" ".classpath.DISABLED"
    echo Found .classpath file. Renamed to .classpath.DISABLED.
) else (
    echo No .classpath file found. That is OK.
)

echo Cleaning up...

REM Clean up the downloaded zip file
del openjfx-21.0.1_windows-x64_bin-sdk.zip

echo Done.

echo ============================================================================
echo To start the project in VSCode press F5 or press the play button (in the top 
echo right corner).

pause