import Loki from 'lokijs';

const db = new Loki('example.db.json');
const users = db.addCollection('users');

users.insert({ name: 'Alice', age: 30 });
console.log(users.find({ age: { $gt: 20 } }));