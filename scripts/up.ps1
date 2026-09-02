# One-click Docker startup for study project
# Usage:
#   .\scripts\up.ps1              # full build (Maven inside Docker)
#   .\scripts\up.ps1 -UseLocalJar # use pre-built jar (faster)

param(
    [switch]$UseLocalJar,
    [switch]$Prod
)

$Root = Split-Path -Parent $PSScriptRoot
Set-Location $Root

$ComposeFiles = @("-f", "docker-compose.yml")
if ($UseLocalJar) {
    $ComposeFiles += @("-f", "docker-compose.jar.yml")
}
if ($Prod) {
    $ComposeFiles += @("-f", "docker-compose.prod.yml")
}

if ($UseLocalJar) {
    Write-Host "==> mvn package" -ForegroundColor Cyan
    Push-Location admin-system
    mvn clean package -DskipTests -q
    if ($LASTEXITCODE -ne 0) {
        Pop-Location
        exit $LASTEXITCODE
    }
    Pop-Location

    Write-Host "==> docker compose (local jar)" -ForegroundColor Cyan
    docker compose @ComposeFiles up -d --build
} else {
    Write-Host "==> docker compose (full build)" -ForegroundColor Cyan
    docker compose @ComposeFiles up -d --build
}

if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

Write-Host ""
Write-Host "==> status" -ForegroundColor Green
docker compose ps
Write-Host ""
Write-Host "Web:    http://localhost" -ForegroundColor Green
Write-Host "Health: http://localhost/api/health" -ForegroundColor Green
Write-Host "Actuator: http://localhost/actuator/health" -ForegroundColor Green
Write-Host "Doc:    http://localhost/doc.html" -ForegroundColor Green
if (-not $Prod) {
    Write-Host "Tip:    docker compose -f docker-compose.yml -f docker-compose.dev-api.yml up -d  # 额外暴露 8080 调试" -ForegroundColor Yellow
}
