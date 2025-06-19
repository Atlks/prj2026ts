
// import { Low } from 'lowdb';
// import { JSONFile } from 'lowdb/node'; // ✅ 正确导入方式
// type Data = {
//   users: { name: string; age: number }[];
// };
// const adapter = new JSONFile<Data>('dbx.json');
// const defaultData: Data = { users: [] };
// const db = new Low<Data>(adapter); // 👈 第二个参数是默认数据
// await db.read();
// db.data ||= { users: [] };
// db.data.users.push({ name: 'Alice', age: 30 });
// await db.write();