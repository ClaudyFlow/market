import os
import re

def fix_files(directory):
    for root, dirs, files in os.walk(directory):
        for file in files:
            if file.endswith('.vue'):
                filepath = os.path.join(root, file)
                try:
                    with open(filepath, 'r', encoding='utf-8') as f:
                        content = f.read()
                    
                    # 修复 v-model 中的方括号语法 - 处理带空格的情况
                    content = re.sub(r'v-model="(\w+)\["([^"]+)"\]"', r'v-model="\1.\2"', content)
                    
                    # 修复所有中文标点
                    content = content.replace('\u3001', ',')  # 顿号
                    content = content.replace('\u3002', '.')  # 句号
                    content = content.replace('\uFF1A', ':')  # 冒号
                    content = content.replace('\uFF0C', ',')  # 逗号
                    content = content.replace('\uFF1B', ';')  # 分号
                    content = content.replace('\uFF08', '(')  # 左括号
                    content = content.replace('\uFF09', ')')  # 右括号
                    
                    with open(filepath, 'w', encoding='utf-8') as f:
                        f.write(content)
                    
                    print(f'Fixed: {filepath}')
                except Exception as e:
                    print(f'Error {filepath}: {e}')

fix_files('./src')
print('Done!')
