Write-Host "====================================================================" -ForegroundColor Cyan
Write-Host "🚀 Building Breaking Chains Debug APK & Uploading to Firebase App Distribution..." -ForegroundColor Cyan
Write-Host "====================================================================" -ForegroundColor Cyan

.\gradlew.bat assembleDebug appDistributionUploadDebug

if ($LASTEXITCODE -eq 0) {
    Write-Host "====================================================================" -ForegroundColor Green
    Write-Host "🎉 Upload Successful! Check your email or Firebase App Tester on your phone." -ForegroundColor Green
    Write-Host "====================================================================" -ForegroundColor Green
} else {
    Write-Host "====================================================================" -ForegroundColor Red
    Write-Host "❌ Upload failed. Please ensure Firebase CLI is logged in via 'firebase login' or FIREBASE_TOKEN is set." -ForegroundColor Red
    Write-Host "====================================================================" -ForegroundColor Red
}
