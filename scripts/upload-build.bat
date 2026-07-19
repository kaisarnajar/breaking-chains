@echo off
echo ====================================================================
echo 🚀 Building Breaking Chains Debug APK & Uploading to Firebase App Distribution...
echo ====================================================================

call .\gradlew.bat assembleDebug appDistributionUploadDebug

if %ERRORLEVEL% EQU 0 (
    echo ====================================================================
    echo 🎉 Upload Successful! Check your email or Firebase App Tester on your phone.
    echo ====================================================================
) else (
    echo ====================================================================
    echo ❌ Upload failed. Please ensure Firebase CLI is logged in via 'firebase login'
    echo or FIREBASE_TOKEN is configured in environment variables.
    echo ====================================================================
)
