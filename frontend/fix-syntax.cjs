const fs = require('fs')
const path = require('path')

// 读取目录并修复文件
function fixFiles (dir) {
  const files = fs.readdirSync(dir)
  
  files.forEach(file => {
    const filePath = path.join(dir, file)
    const stat = fs.statSync(filePath)
    
    if (stat.isDirectory()) {
      fixFiles(filePath)
    } else if (file.endsWith('.vue') || file.endsWith('.ts')) {
      let content = fs.readFileSync(filePath, 'utf-8')
      
      // 修复错误的可选链方括号语法
      content = content.replace(/\?\. \s*\[/g, '?[')
      content = content.replace(/\.value\?\[/g, '.value?.[')
      
      // 修复模板中的点号访问 - 使用更安全的中文变量名（不含空格和特殊字符）
      // 将 ["xxx"] 改回 .xxx 格式（对于简单变量名）
      content = content.replace(/\["([^"\s]+)"\]/g, '.$1')
      
      fs.writeFileSync(filePath, content, 'utf-8')
      console.log('Fixed:', filePath)
    }
  })
}

fixFiles('./src')
console.log('Done!')
