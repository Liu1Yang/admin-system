# Windows 安装 WSL2（供 Docker Desktop 使用）

## 最快方式（推荐）

1. **右键「开始」→ Windows PowerShell（管理员）** 或 **终端（管理员）**
2. 执行：

```powershell
wsl --install
```

3. **重启电脑**
4. 重启后会自动打开 Ubuntu，按提示设置 **Linux 用户名和密码**
5. 确认 WSL2：

```powershell
wsl --set-default-version 2
wsl --status
```

6. 安装 [Docker Desktop for Windows](https://www.docker.com/products/docker-desktop/)
7. Docker Desktop → **Settings**：
   - General → ✅ Use the WSL 2 based engine
   - Resources → WSL Integration → ✅ Ubuntu

8. 验证：

```powershell
docker compose version
cd D:\project\study
docker compose up -d --build
```

---

## 备用：运行项目内脚本

管理员 PowerShell：

```powershell
Set-ExecutionPolicy -Scope Process Bypass -Force
D:\project\study\docs\install-wsl.ps1
```

然后 **重启电脑**。

---

## 常见问题

### 提示「需要提升」或 exit code 740

必须用 **管理员** 打开 PowerShell，普通终端无法安装 WSL。

### `wsl --install` 失败 / 无法下载

```powershell
wsl --install --web-download -d Ubuntu
```

或手动启用功能后重启：

```powershell
dism.exe /online /enable-feature /featurename:Microsoft-Windows-Subsystem-Linux /all /norestart
dism.exe /online /enable-feature /featurename:VirtualMachinePlatform /all /norestart
# 重启后再 wsl --install
```

### BIOS 未开启虚拟化

任务管理器 → 性能 → CPU → 看「虚拟化」是否为 **已启用**。  
若为否，进 BIOS 开启 **Intel VT-x / AMD-V**。

### Docker 仍提示 WSL

- 确认 `wsl --status` 显示 **默认版本: 2**
- Docker Desktop 完全退出后重开
- Windows 更新到较新版本（Win10 19041+ / Win11）

### 与本地 MySQL 端口冲突

Docker 起来后占用 3306/6379，请先停掉本机 MySQL/Redis 服务，或改 `docker-compose.yml` 端口。

---

## 安装完成后

回到 [docker.md](./docker.md) 执行 Day32 验收。
