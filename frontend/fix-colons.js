import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

function replaceChineseColons(dir) {
  const files = fs.readdirSync(dir);
  
  files.forEach(file => {
    const filePath = path.join(dir, file);
    const stat = fs.statSync(filePath);
    
    if (stat.isDirectory()) {
      replaceChineseColons(filePath);
    } else if (file.endsWith('.vue') || file.endsWith('.ts')) {
      let content = fs.readFileSync(filePath, 'utf-8');
      const newContent = content.replace(/:/g, ':');
      if (content !== newContent) {
        fs.writeFileSync(filePath, newContent, 'utf-8');
        console.log(`Fixed: ${filePath}`);
      }
    }
  });
}

replaceChineseColons(path.join(__dirname, 'src'));
