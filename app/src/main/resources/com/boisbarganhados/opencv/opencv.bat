@echo off
setlocal

rem Change this path to the directory where OpenCV is installed
set "OPENCV_DIR=\x64"

rem Check if the directory exists
if not exist "%OPENCV_DIR%" (
    echo OpenCV directory does not exist: %OPENCV_DIR%
    exit /b 1
)

rem Check if the OpenCV bin directory exists
if not exist "%OPENCV_DIR%\bin" (
    echo OpenCV bin directory does not exist: %OPENCV_DIR%\bin
    exit /b 1
)

rem Add OpenCV bin directory to the PATH environment variable
set "PATH=%OPENCV_DIR%\bin;%PATH%"

echo OpenCV added to PATH environment variable.
echo PATH: %PATH%

endlocal
