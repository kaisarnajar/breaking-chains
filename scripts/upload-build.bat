@echo off
setlocal enabledelayedexpansion

echo ====================================================================
echo Building Breaking Chains Debug APK and Uploading to Firebase...
echo ====================================================================

:: Auto-detect JAVA_HOME if not set
if "%JAVA_HOME%"=="" (
    if exist "C:\Program Files\Android\Android Studio\jbr" (
        set "JAVA_HOME=C:\Program Files\Android\Android Studio\jbr"
        set "PATH=!JAVA_HOME!\bin;%PATH%"
        echo [INFO] Auto-detected Java JDK from Android Studio jbr: !JAVA_HOME!
    ) else if exist "C:\Program Files\Android\Android Studio\jre" (
        set "JAVA_HOME=C:\Program Files\Android\Android Studio\jre"
        set "PATH=!JAVA_HOME!\bin;%PATH%"
        echo [INFO] Auto-detected Java JDK from Android Studio jre: !JAVA_HOME!
    ) else (
        echo [WARNING] JAVA_HOME is not set. Please set JAVA_HOME in environment variables.
    )
) else (
    set "PATH=%JAVA_HOME%\bin;%PATH%"
)

echo ====================================================================
echo Starting Gradle build and App Distribution upload...
echo ====================================================================

call .\gradlew.bat clean assembleDebug appDistributionUploadDebug

if %ERRORLEVEL% EQU 0 (
    echo ====================================================================
    echo SUCCESS: Upload complete! Check your email or Firebase App Tester on your phone.
    echo ====================================================================
) else (
    echo ====================================================================
    echo FAILED: Upload failed. If authentication failed, please run 'firebase login'
    echo or set FIREBASE_TOKEN in your environment.
    echo ====================================================================
)
