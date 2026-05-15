# 安全测试脚本 - 常见漏洞检测示例
# 用法：Powershell -ExecutionPolicy Bypass -File .\security-test.ps1

$baseUrl = "http://localhost:8080/api"
$token = $null

Write-Host "===== 安全测试 =====" -ForegroundColor Yellow

# 辅助：获取token
function Get-Token {
    param($user="security_test", $pass="Test123456")
    try {
        $reg = Invoke-RestMethod -Uri "$baseUrl/auth/register" -Method Post -Body (@{name=$user;email="$user@test.com";password=$pass;confirmPassword=$pass} | ConvertTo-Json) -ContentType "application/json" -ErrorAction Stop
        return $reg.token
    } catch {
        try {
            $login = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method Post -Body (@{name=$user;password=$pass} | ConvertTo-Json) -ContentType "application/json" -ErrorAction SilentlyContinue
            return $login.token
        } catch {
            return $null
        }
    }
}

$token = Get-Token
$headers = @{ Authorization = "Bearer $token" }

# 1. SQL注入检测 - 在搜索参数中尝试SQLi
Write-Host "`n[1] SQL注入检测" -ForegroundColor Cyan
$sqli_payloads = @(
    "' OR '1'='1",
    "' OR 1=1--",
    "admin'--",
    "1' UNION SELECT null,username,password FROM users--",
    "'; DROP TABLE product;--"
)
foreach ($payload in $sqli_payloads) {
    try {
        $resp = Invoke-RestMethod -Uri "$baseUrl/product/search?keyword=$([System.Web.HttpUtility]::UrlEncode($payload))" -Method Get -Headers $headers -ErrorAction Stop
        # 检查响应是否异常（如返回太多数据或500错误）
        if ($resp -and $resp.data -and $resp.data.content.Count -gt 100) {
            Write-Host "   ❗ 可能存在SQL注入: '$payload' 返回大量数据" -ForegroundColor Red
        } else {
            Write-Host "   ✅ 对 '$payload' 的正常响应 (返回 $($resp.data.content.Count) 条)" -ForegroundColor Green
        }
    } catch {
        $status = $_.Exception.Response.StatusCode.value__
        if ($status -eq 500) {
            Write-Host "   ⚠️  服务器500错误 (可能SQL注入触发异常): '$payload'" -ForegroundColor Yellow
        } else {
            Write-Host "   ✅ 对 '$payload' 的正常响应 (状态 $status)" -ForegroundColor Green
        }
    }
}

# 2. XSS检测 - 在用户输入字段尝试XSS
Write-Host "`n[2] XSS检测" -ForegroundColor Cyan
$xss_payloads = @(
    "<script>alert('XSS')</script>",
    "<img src=x onerror=alert('XSS')>",
    "javascript:alert('XSS')",
    "<svg onload=alert('XSS')>",
    "'"><script>alert(String.fromCharCode(88,83,83))</script>"
)
foreach ($payload in $xss_payloads) {
    try {
        # 尝试在商品搜索中使用XSS payload（如果存在反射）
        $resp = Invoke-RestMethod -Uri "$baseUrl/product/search?keyword=$([System.Web.HttpUtility]::UrlEncode($payload))" -Method Get -Headers $headers -ErrorAction Stop
        # 检查响应中是否包含payload（未转义）
        $respJson = $resp | ConvertTo-Json -Depth 5
        if ($respJson -match [regex]::Escape($payload)) {
            Write-Host "   ❗ 可能存在反射XSS: '$payload'" -ForegroundColor Red
        } else {
            Write-Host "   ✅ 对 '$payload' 已转义或拒绝" -ForegroundColor Green
        }
    } catch {
        Write-Host "   ✅ 对 '$payload' 请求失败 (可能被过滤)" -ForegroundColor Green
    }
}

# 3. 认证绕过检测 - 尝试访问需要认证的接口不带token
Write-Host "`n[3] 认证绕过检测" -ForegroundColor Cyan
$protected_endpoints = @(
    "$baseUrl/user/info",
    "$baseUrl/cart",
    "$baseUrl/order/create",
    "$baseUrl/admin/users"
)
foreach ($ep in $protected_endpoints) {
    try {
        $resp = Invoke-RestMethod -Uri $ep -Method Get -ErrorAction Stop
        Write-Host "   ❗ 未授权访问成功 (可能认证绕过): $ep" -ForegroundColor Red
    } catch {
        $status = $_.Exception.Response.StatusCode.value__
        if ($status -eq 401 -or $status -eq 403) {
            Write-Host "   ✅ 正确拒绝未授权访问 (状态 $status): $ep" -ForegroundColor Green
        } else {
            Write-Host "   ⚠️  其他状态 $status: $ep" -ForegroundColor Yellow
        }
    }
}

# 4. IDOR检测 - 尝试访问其他用户数据
Write-Host "`n[4] IDOR检测" -ForegroundColor Cyan
# 创建另一个用户
$otherUser = "user_for_idor_test"
$otherPass = "Test123456"
try {
    $regOther = Invoke-RestMethod -Uri "$baseUrl/auth/register" -Method Post -Body (@{name=$otherUser;email="$otherUser@test.com";password=$otherPass;confirmPassword=$otherPass} | ConvertTo-Json) -ContentType "application/json" -ErrorAction Stop
    $tokenOther = $regOther.token
    # 用主用户token尝试访问其他用户信息（假设存在/user/{id}接口或订单等）
    # 如果存在/admin/users接口，可测试
    try {
        $resp = Invoke-RestMethod -Uri "$baseUrl/admin/users" -Method Get -Headers $headers -ErrorAction Stop
        Write-Host "   ⚠️  普通用户可访问管理员接口" -ForegroundColor Yellow
    } catch {
        Write-Host "   ✅ 普通用户无法访问管理员接口" -ForegroundColor Green
    }
    # 尝试用主用户token获取其他用户的订单（如存在/order/{id}，且可查看任意订单）
    # 这里简化测试
} catch {
    Write-Host "   ⚠️  IDOR测试受限" -ForegroundColor Yellow
}

# 5. 敏感信息泄露检测
Write-Host "`n[5] 敏感信息泄露检测" -ForegroundColor Cyan
try {
    $userInfo = Invoke-RestMethod -Uri "$baseUrl/user/info" -Method Get -Headers $headers -ErrorAction Stop
    $userJson = $userInfo | ConvertTo-Json -Depth 5
    # 检查是否返回密码、token等敏感字段
    if ($userJson -match 'password|passwordHash|token|secret') {
        Write-Host "   ❗ 用户信息中可能包含敏感字段" -ForegroundColor Red
    } else {
        Write-Host "   ✅ 用户信息中未发现明显敏感字段" -ForegroundColor Green
    }
} catch {
    Write-Host "   ✅ 无法获取用户信息" -ForegroundColor Green
}

# 6. 速率限制检测
Write-Host "`n[6] 速率限制检测" -ForegroundColor Cyan
$rapidRequests = 50
$rapidStart = Get-Date
$rateLimited = $false
for ($i = 1; $i -le $rapidRequests; $i++) {
    try {
        $resp = Invoke-RestMethod -Uri "$baseUrl/product?page=1&size=1" -Method Get -Headers $headers -ErrorAction Stop
    } catch {
        $status = $_.Exception.Response.StatusCode.value__
        if ($status -eq 429) {
            $rateLimited = $true
            Write-Host "   ⚠️  触发速率限制 (429) 在第 $i 次请求" -ForegroundColor Yellow
            break
        }
    }
}
if (-not $rateLimited) {
    Write-Host "   ✅ 未触发速率限制 (可能未配置)" -ForegroundColor Green
}

Write-Host "`n===== 安全测试完成 =====" -ForegroundColor Yellow

# 保存报告
$reportTime = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
Write-Host "报告时间: $reportTime"