#!/usr/bin/env nu

# 启动前端服务 - Nginx

def main [] {
    print "[启动前端] 开始检查 Nginx..."

    # 检查 Nginx
    let nginx_check = (try { ^nginx -v | complete } catch { null })
    if $nginx_check == null or $nginx_check.exit_code != 0 {
        print "[错误] Nginx 未找到"
        return
    }

    # 启动 Nginx
    ^nginx -s stop 2>nul | complete | ignore
    sleep 1sec
    ^nginx | complete | ignore
    
    let nginx_test = (try { ^nginx -t | complete } catch { null })
    if $nginx_test == null or $nginx_test.exit_code != 0 {
        print "[错误] Nginx 启动失败"
        return
    }

    print "[成功] 前端服务已启动 (http://localhost:80)"
}
