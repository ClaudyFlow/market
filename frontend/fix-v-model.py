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
                    
                    # 修复 v-model 中的方括号语法 - 将 ["xxx"] 改为.xxx
                    # 但只修复简单的情况
                    content = re.sub(r'v-model="(\w+)\["(\w+)"\]"', r'v-model="\1.\2"', content)
                    
                    with open(filepath, 'w', encoding='utf-8') as f:
                        f.write(content)
                    
                    print(f'Fixed: {filepath}')
                except Exception as e:
                    print(f'Error {filepath}: {e}')

fix_files('./src')
print('Done!')
