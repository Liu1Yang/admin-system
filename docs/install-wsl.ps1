# WSL2 + Ubuntu 安装脚本（需「以管理员身份运行 PowerShell」）
# 用法：右键 PowerShell → 以管理员身份运行 → 执行：
#   Set-ExecutionPolicy -Scope Process Bypass -Force
#   D:\project\study\docs\install-wsl.ps1

$ErrorActionPreference = "Stop"

Write-Host "=== 1. 启用 WSL 与虚拟机平台 ===" -ForegroundColor Cyan
dism.exe /online /enable-feature /featurename:Microsoft-Windows-Subsystem-Linux /all /norestart
dism.exe /online /enable-feature /featurename:VirtualMachinePlatform /all /norestart

Write-Host "=== 2. 安装 WSL2 内核（若已安装会跳过）===" -ForegroundColor Cyan
$wslUpdate = "$env:TEMP\wsl_update_x64.msi"
if (-not (Get-Command wsl -ErrorAction SilentlyContinue)) {
    Write-Host "未找到 wsl 命令，请先重启后再运行 wsl --install" -ForegroundColor Yellow
} else {
    wsl --install --web-download -d Ubuntu --no-launch
    wsl --set-default-version 2
}

Write-Host ""
Write-Host "=== 完成 ===" -ForegroundColor Green
Write-Host "请重启电脑，重启后：" -ForegroundColor Yellow
Write-Host "  1. 打开「Ubuntu」完成用户名/密码初始化"
Write-Host "  2. 运行: wsl --set-default-version 2"
Write-Host "  3. 安装 Docker Desktop: https://www.docker.com/products/docker-desktop/"
Write-Host "  4. Docker Desktop → Settings → 勾选 Use the WSL 2 based engine"
Write-Host "  5. Resources → WSL Integration → 启用 Ubuntu"
Write-Host ""
Read-Host "按 Enter 退出（重启前请先保存工作）"
