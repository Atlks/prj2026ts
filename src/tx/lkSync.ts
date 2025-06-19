import fs from 'fs';
import Loki from 'lokijs';

const dbFile = 'example.db.json';

const db = new Loki(dbFile);

// 加载数据库（同步）
if (fs.existsSync(dbFile)) {
  const raw = fs.readFileSync(dbFile, 'utf-8');
  db.loadJSON(raw);
}

// 获取或创建集合
const users = db.getCollection('users') || db.addCollection('users');

// 插入数据
users.insert({ name: 'Alice2', age: 30 });

// 查询数据
console.log(users.find({ age: { $gt: 20 } }));

// 保存数据库（同步）
const serialized = db.serialize();
fs.writeFileSync(dbFile, serialized, 'utf-8');

console.log('✅ 数据已同步保存');


console.log('程序将在 5 秒后退出...');
setTimeout(() => {
  console.log('再见 👋');
  process.exit(0); // 正常退出
}, 5000);