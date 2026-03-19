import os

def fix_files(directory):
    for root, dirs, files in os.walk(directory):
        for file in files:
            if file.endswith('.vue') or file.endswith('.ts'):
                filepath = os.path.join(root, file)
                try:
                    with open(filepath, 'r', encoding='utf-8') as f:
                        content = f.read()
                    
                    # 替换中文标点符号为英文
                    content = content.replace('\uFF0C', ',')  # 中文逗号
                    content = content.replace('\uFF1B', ';')  # 中文分号
                    content = content.replace('\uFF08', '(')  # 中文左括号
                    content = content.replace('\uFF09', ')')  # 中文右括号
                    content = content.replace('\uFF5B', '{')  # 中文左花括号
                    content = content.replace('\uFF5D', '}')  # 中文右花括号
                    content = content.replace('\uFF3B', '[')  # 中文左方括号
                    content = content.replace('\uFF3D', ']')  # 中文右方括号
                    content = content.replace('\uFF1F', '?')  # 中文问号
                    content = content.replace('\uFF01', '!')  # 中文感叹号
                    
                    with open(filepath, 'w', encoding='utf-8') as f:
                        f.write(content)
                    
                    print(f'Fixed: {filepath}')
                except Exception as e:
                    print(f'Error {filepath}: {e}')

fix_files('./src')
print('Done!')
