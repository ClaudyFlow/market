#!/usr/bin/env nu

# 启动数据库服务 - PostgreSQL

def main [] {
    let pg_bin = "C:/Program Files/PostgreSQL/18/bin"
    let pg_data = "C:/Program Files/PostgreSQL/18/data"

    print "[启动数据库] 开始检查 PostgreSQL..."

    # 检查 PostgreSQL 是否安装
    let pg_isready_path = $"($pg_bin)/pg_isready.exe"
    let is_installed = (try { ^$pg_isready_path | complete } catch { null })

    if $is_installed == null or $is_installed.exit_code != 0 {
        print "[错误] PostgreSQL 未安装，正在使用 winget 安装..."
        
        let install_result = (^winget install -e --id PostgreSQL.PostgreSQL.18 --accept-package-agreements --accept-source-agreements | complete)
        
        if $install_result.exit_code != 0 {
            print "[错误] PostgreSQL 安装失败"
            return
        }
        print "[成功] PostgreSQL 安装完成"
    }

    # 检查 PostgreSQL 是否运行
    let ready = (do { ^$pg_isready_path } | complete)
    
    if $ready.exit_code != 0 {
        print "[启动] PostgreSQL 未运行，正在启动..."
        let pg_ctl = $"($pg_bin)/pg_ctl.exe"
        ^$pg_ctl start -D $pg_data -l $"($pg_data)/log/postgresql.log" | complete | ignore
        sleep 3sec
        
        let ready2 = (do { ^$pg_isready_path } | complete)
        if $ready2.exit_code != 0 {
            print "[错误] PostgreSQL 启动失败"
            return
        }
        print "[成功] PostgreSQL 已启动"
    }

    # 验证数据库连接
    $env.PGPASSWORD = "market"
    let psql = $"($pg_bin)/psql.exe"
    let conn_test = (do { ^$psql -U market -d market -c "SELECT 1;" } | complete)
    
    if $conn_test.exit_code != 0 {
        print "[错误] 无法连接到 market 数据库"
        return
    }

    print "[成功] 数据库服务已就绪"
}
