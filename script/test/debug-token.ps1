$timestamp = Get-Date -Format "yyyyMMddHHmmss"
$testUserName = "testuser_$timestamp"
$testPassword = "Test123456"

Write-Host "=== 测试用户注册 ==="
$regBody = @{
    name = $testUserName
    email = "test${timestamp}@example.com"
    password = $testPassword
    confirmPassword = $testPassword
} | ConvertTo-Json

$regResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/register" -Method POST -Body $regBody -ContentType "application/json" -ErrorAction SilentlyContinue
Write-Host "注册响应:"
$regResponse | ConvertTo-Json -Depth 3

Write-Host "`n=== 检查Token内容 ==="
if ($regResponse.token) {
    $token = $regResponse.token
    $dotCount = ($token -split '\.').Count - 1
    Write-Host "Token长度: $($token.Length)"
    Write-Host "Token中点的数量: $dotCount"
    Write-Host "Token前50字符: $($token.Substring(0, [Math]::Min(50, $token.Length)))"
    Write-Host "Token后50字符: $($token.Substring([Math]::Max(0, $token.Length-50)))"
} else {
    Write-Host "Token为null!"
}

Write-Host "`n=== 测试登录(set-cookie?) ==="
$loginBody = @{ name = $testUserName; password = $testPassword } | ConvertTo-Json
$loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method POST -Body $loginBody -ContentType "application/json" -ErrorAction SilentlyContinue
Write-Host "登录响应:"
$loginResponse | ConvertTo-Json -Depth 3
