#!/usr/bin/env nu

# ========================================
# 市场平台 - 完整编译脚本 (Nushell 版)
# Market Platform - Full Compile
# ========================================

let start_time = (date now)

# 切换到项目根目录
cd ($env.FILE_PWD | path join '..' '..')

print "========================================"
print "     市场平台 - 完整编译脚本"
print "     Market Platform - Full Compile"
print "========================================"
print ""

# ========================================
# [1/8] 检查 Java
# ========================================
print "[1/8] 检查 Java 环境..."
if (which java | is-empty) {
    print "[错误] 未找到 Java"
    print "[操作] 请访问: https://www.oracle.com/java/technologies/downloads/"
    exit 1
}
print "[成功] Java 已安装"
^java --version | lines | first | ignore
print ""

# ========================================
# [2/8] 检查 mvnd
# ========================================
print "[2/8] 检查 Maven Daemon (mvnd)..."
if (which mvnd | is-empty) {
    print "[错误] 未找到 mvnd"
    print "[操作] 请访问: https://github.com/mvndaemon/mvnd"
    exit 1
}
print "[成功] mvnd 已安装"
print ""

# ========================================
# [3/8] 检查 Node.js
# ========================================
print "[3/8] 检查 Node.js..."
if (which node | is-empty) {
    print "[警告] 未找到 Node.js"
    print "[操作] 正在使用 winget 安装 Node.js..."
    let install_result = (^winget install OpenJS.NodeJS | complete)
    if $install_result.exit_code != 0 {
        print "[错误] Node.js 安装失败"
        print "[操作] 请访问: https://nodejs.org/"
        exit 1
    }
    print "[成功] Node.js 安装完成"
} else {
    print "[成功] Node.js 已安装"
    ^node --version | ignore
    ^npm --version | ignore
}
print ""

# ========================================
# [4/8] 检查 Nginx
# ========================================
print "[4/8] 检查 Nginx..."
if ('frontend/nginx/nginx.exe' | path exists) {
    print "[成功] Nginx 已存在于 frontend/nginx/"
    ^frontend/nginx/nginx.exe -v | ignore
    print "[提示] Nginx 官网: https://nginx.org/"
} else {
    print "[错误] 未找到 frontend/nginx/nginx.exe"
    print "[提示] Nginx 已预置在仓库中，请确保文件完整"
    print "[操作] 请访问: https://nginx.org/"
    exit 1
}
print ""

# ========================================
# [5/8] 检查 PostgreSQL
# ========================================
print "[5/8] 检查 PostgreSQL..."
let pg_check = (^sc query PostgreSQL | complete)
if $pg_check.exit_code == 0 {
    print "[成功] PostgreSQL 服务已安装"
    ^net start PostgreSQL | ignore
    print "[成功] PostgreSQL 已启动"
} else {
    print "[警告] 未检测到 PostgreSQL 服务"
    print "[提示] 请访问: https://www.postgresql.org/"
}
print ""

# ========================================
# [6/8] 检查 Redis
# ========================================
print "[6/8] 检查 Redis..."
let redis_check = (^sc query Redis | complete)
if $redis_check.exit_code == 0 {
    print "[成功] Redis 服务已安装"
    ^net start Redis | ignore
    print "[成功] Redis 已启动"
} else {
    print "[警告] 未检测到 Redis 服务"
    print "[提示] 请访问: https://redis.io/"
}
print ""

# ========================================
# [7/8] 编译前端
# ========================================
print "========================================"
print "[7/8] 编译前端"
print "========================================"
print ""

cd ($env.FILE_PWD | path join '..' '..' 'frontend')

# 检查依赖
if not ('node_modules' | path exists) {
    print "[安装] 正在安装前端依赖..."
    let install_result = (^npm install | complete)
    if $install_result.exit_code != 0 {
        print "[错误] 依赖安装失败"
        exit 1
    }
}

# 编译前端
print "[构建] 正在编译前端..."
let frontend_result = (^npm run build | complete)
if $frontend_result.exit_code != 0 {
    print "[错误] 前端编译失败"
    exit 1
}
print "[成功] 前端编译完成"
print ""

# ========================================
# [8/8] 编译后端
# ========================================
print "========================================"
print "[8/8] 编译后端"
print "========================================"
print ""

cd ($env.FILE_PWD | path join '..' '..')

# 清理
if ('target' | path exists) {
    print "[清理] 正在清理旧编译..."
    rm -r target
}

# 编译
print "[编译] 正在编译后端..."
let backend_result = (^mvnd clean compile -DskipTests | complete)
if $backend_result.exit_code != 0 {
    print "[错误] 后端编译失败"
    exit 1
}
print "[成功] 后端编译完成"
print ""

# ========================================
# 重启 Nginx
# ========================================
print "========================================"
print "重启 Nginx"
print "========================================"
print ""

cd ($env.FILE_PWD | path join '..' '..' 'frontend' 'nginx')
if ('nginx.exe' | path exists) {
    print "[重启] 正在重启 Nginx..."
    ^nginx.exe -s stop | ignore
    sleep 2sec
    ^nginx.exe | ignore
    print "[成功] Nginx 已重启"
}
print ""

let end_time = (date now)
let duration = (($end_time - $start_time) | into int | math abs)
let seconds = ($duration / 1000000000 | math round)

print "========================================"
print "     完整编译成功!"
print "========================================"
print ""
print "编译信息:"
print "  - 前端: Vite + Vue 3"
print "  - 后端: Spring Boot + mvnd"
print "  - 服务器: Nginx (预置)"
print "  - 数据库: PostgreSQL"
print "  - 缓存: Redis"
print $"  - 总耗时: ($seconds) 秒"
print ""
print "访问地址:"
print "  - 前端: http://localhost:80"
print "  - 后端 API: http://localhost:8080/api/"
print "  - 开发服务器: http://localhost:5173"
print ""
