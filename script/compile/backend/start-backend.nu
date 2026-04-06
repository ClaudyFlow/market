#!/usr/bin/env nu

# 启动后端服务 (数据库 + Spring Boot)

def main [] {
    print "[启动后端] 开始启动后端服务..."

    # 启动数据库
    let script_dir = ($env.FILE_PWD | default "")
    let db_script = $"($script_dir)start-database.nu"
    
    if not ($db_script | path exists) {
        print "[错误] 未找到 start-database.nu"
        return
    }
    
    nu $db_script
    
    # 检查 mvnd
    let mvnd_check = (try { ^mvnd --version | complete } catch { null })
    if $mvnd_check == null or $mvnd_check.exit_code != 0 {
        print "[错误] mvnd 未找到，请安装 mvnd"
        return
    }

    # 启动 Spring Boot
    let project_root = ($script_dir | path dirname | path dirname | path dirname)
    
    if not ($"($project_root)/pom.xml" | path exists) {
        print $"[错误] 未找到 pom.xml，路径: ($project_root)"
        return
    }

    cd $project_root
    print "[启动] 正在启动 Spring Boot 后端服务..."
    print "[提示] 按 Ctrl+C 可停止服务"
    ^mvnd spring-boot:run
}
