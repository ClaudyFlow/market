import os
import re

def fix_files(directory):
    for root, dirs, files in os.walk(directory):
        for file in files:
            if file.endswith('.vue') or file.endswith('.ts'):
                filepath = os.path.join(root, file)
                try:
                    with open(filepath, 'r', encoding='utf-8') as f:
                        content = f.read()
                    
                    # 替换带空格的变量名为不带空格的
                    content = re.sub(r'用户 ID', '用户 ID', content)
                    content = re.sub(r'商品 ID', '商品 ID', content)
                    content = re.sub(r'商家 ID', '商家 ID', content)
                    content = re.sub(r'订单 ID', '订单 ID', content)
                    
                    # 修复方括号访问 - 将 ["xxx"] 改为.xxx (对于简单变量)
                    content = re.sub(r'\["([^"\s]+)"\]', r'.\1', content)
                    
                    with open(filepath, 'w', encoding='utf-8') as f:
                        f.write(content)
                    
                    print(f'Fixed: {filepath}')
                except Exception as e:
                    print(f'Error {filepath}: {e}')

fix_files('./src')
print('Done!')
