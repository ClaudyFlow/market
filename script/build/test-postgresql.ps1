#!/usr/bin/env pwsh
# PostgreSQL 连接快速验证脚本

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  PostgreSQL Connection Test" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor White

$psqlPaths = @(
    "C:\Program Files\PostgreSQL\18\bin\psql.exe",
    "C:\Program Files\PostgreSQL\10\bin\psql.exe"
)

$psql = $null
foreach ($p in $psqlPaths) {
    if (Test-Path $p) {
        $psql = $p
        break
    }
}

if (-not $psql) {
    Write-Host "ERROR: psql.exe not found" -ForegroundColor Red
    exit 1
}

Write-Host "Using: $psql" -ForegroundColor Gray

# Test 1: postgres user (trust)
Write-Host ""
Write-Host "[Test 1] Connect as postgres (trust mode)..." -ForegroundColor Yellow
try {
    $result = & $psql -U postgres -d postgres -c "SELECT current_user, version();" 2>&1
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✓ Connected as postgres" -ForegroundColor Green
        $result | Out-Host
    } else {
        Write-Host "✗ Failed: $result" -ForegroundColor Red
    }
} catch {
    Write-Host "✗ Exception: $_" -ForegroundColor Red
}

# Test 2: market user with password
Write-Host ""
Write-Host "[Test 2] Connect as market (password: market)..." -ForegroundColor Yellow
$env:PGPASSWORD = "market"
try {
    $result = & $psql -U market -d market -c "SELECT current_user, version();" 2>&1
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✓ Connected as market" -ForegroundColor Green
        $result | Out-Host
    } else {
        Write-Host "✗ Failed: $result" -ForegroundColor Red
    }
} catch {
    Write-Host "✗ Exception: $_" -ForegroundColor Red
}

# Test 3: List databases
Write-Host ""
Write-Host "[Test 3] List databases..." -ForegroundColor Yellow
try {
    $result = & $psql -U postgres -d postgres -c "\l" 2>&1
    if ($LASTEXITCODE -eq 0) {
        $result | Select-String -Pattern "market|Name|^ " | Select-Object -First 10
    }
} catch {
    Write-Host "✗ Exception: $_" -ForegroundColor Red
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Read-Host "Press Enter to exit"
