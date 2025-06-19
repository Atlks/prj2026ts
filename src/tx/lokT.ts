import Loki from 'lokijs';

const db = new Loki('example.db.json', {
  persistenceMethod: 'fs',       // 使用文件系统保存
  autosave: true,                // 自动保存
  autosaveInterval: 2000,        // 每 4 秒保存一次
  autoload: true 
});

  const users = db.getCollection('users') || db.addCollection('users'); // ✅ 避免重复添加




users.insert({ name: 'Alice1', age: 30 });
console.log(users.find({ age: { $gt: 20 } }));
db.saveDatabase((err) => {
  if (err) console.error('保存失败:', err);
  else console.log('保存成功');
});


console.log('程序将在 5 秒后退出...');
setTimeout(() => {
  console.log('再见 👋');
  process.exit(0); // 正常退出
}, 5000);