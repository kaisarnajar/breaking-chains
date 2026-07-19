# Auto-detect JAVA_HOME if missing
if (-not $env:JAVA_HOME) {
    if (Test-Path "C:\Program Files\Android\Android Studio\jbr") {
        $env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
        $env:Path = "$env:JAVA_HOME\bin;" + $env:Path
        Write-Host "[INFO] Auto-detected Java JDK: $env:JAVA_HOME" -ForegroundColor Yellow
    } elseif (Test-Path "C:\Program Files\Android\Android Studio\jre") {
        $env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jre"
        $env:Path = "$env:JAVA_HOME\bin;" + $env:Path
        Write-Host "[INFO] Auto-detected Java JDK: $env:JAVA_HOME" -ForegroundColor Yellow
    }
}

Write-Host "====================================================================" -ForegroundColor Cyan
Write-Host "🚀 Building Breaking Chains Debug APK & Uploading to Firebase App Distribution" -ForegroundColor Cyan
Write-Host "====================================================================" -ForegroundColor Cyan

$buildName = Read-Host "Please enter a name / release note for this build"
if ([string]::IsNullOrWhiteSpace($buildName)) {
    $buildName = "Breaking Chains Test Build"
}

Write-Host "Building APK with release name: '$buildName'..." -ForegroundColor Yellow

.\gradlew.bat clean assembleDebug appDistributionUploadDebug "-PappDistributionReleaseNotes=$buildName"

if ($LASTEXITCODE -eq 0) {
    Write-Host "====================================================================" -ForegroundColor Green
    Write-Host "🎉 Upload Successful! Check your email or Firebase App Tester on your phone." -ForegroundColor Green
    Write-Host "====================================================================" -ForegroundColor Green
} else {
    Write-Host "====================================================================" -ForegroundColor Red
    Write-Host "❌ Upload failed. Please run 'npx firebase-tools login' or set FIREBASE_TOKEN environment variable." -ForegroundColor Red
    Write-Host "====================================================================" -ForegroundColor Red
}
