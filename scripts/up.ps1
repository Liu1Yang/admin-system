# One-click Docker startup for study project
# Usage:
#   .\scripts\up.ps1              # full build (Maven inside Docker)
#   .\scripts\up.ps1 -UseLocalJar # use pre-built jar (faster)

param(
    [switch]$UseLocalJar
)

$Root = Split-Path -Parent $PSScriptRoot
Set-Location $Root

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
    docker compose -f docker-compose.yml -f docker-compose.jar.yml up -d --build
} else {
    Write-Host "==> docker compose (full build)" -ForegroundColor Cyan
    docker compose up -d --build
}

if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

Write-Host ""
Write-Host "==> status" -ForegroundColor Green
docker compose ps
Write-Host ""
Write-Host "Health: http://localhost:8080/api/health" -ForegroundColor Green
Write-Host "Doc:    http://localhost:8080/doc.html" -ForegroundColor Green
