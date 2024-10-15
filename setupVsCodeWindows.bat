@echo off

REM README; Only run this script when initially setting up the vscode
REM environment for the fist time. This script will download javafx
REM and setup the correct environment. Making sure the program can be run
REM using the run and debug tools. The necessary lib will be acquired and
REM linked as a module.

REM Create necessary directories
mkdir ".\lib"
mkdir ".\.vscode"

echo Downloading 'openjfx-21.0.1'...

REM Download the file
powershell -command "(New-Object System.Net.WebClient).DownloadFile('https://download2.gluonhq.com/openjfx/21.0.1/openjfx-21.0.1_windows-x64_bin-sdk.zip', 'openjfx-21.0.1_windows-x64_bin-sdk.zip')"

echo Installing 'openjfx-21.0.1'

REM Extract the contents of the downloaded file
powershell -command "Expand-Archive -Path '.\openjfx-21.0.1_windows-x64_bin-sdk.zip' -DestinationPath '.\lib'"

echo Creating .vscode files

REM Create and populate the launch.json file
echo { > ".\.vscode\launch.json"
echo  "configurations": [ >> ".\.vscode\launch.json"
echo    { >> ".\.vscode\launch.json"
echo      "type": "java", >> ".\.vscode\launch.json"
echo      "name": "Debug (Launch) - Current File", >> ".\.vscode\launch.json"
echo      "request": "launch", >> ".\.vscode\launch.json"
echo      "mainClass": "${file}", >> ".\.vscode\launch.json"
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

echo Cleaning up...

REM Clean up the downloaded zip file
del openjfx-21.0.1_windows-x64_bin-sdk.zip

echo Done.
