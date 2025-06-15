

import { Transaction } from "./tx.ts";
import { Low } from 'lowdb';
import { JSONFile } from 'lowdb/node'
 import * as fs from "fs";
import * as path from "path";
var tx=new Transaction();
tx.amount="88";
tx.transactionId="999"
persist(tx)


/**
 *  直接同步方法，写入文件，保存为json格式
 * @param tx 
 */
export   function persist(tx: any) {
 
  if (!tx.transactionId) {
    throw new Error("Transaction must have a transactionId");
  }

  // JSON 序列化
  const json = JSON.stringify(tx, null, 2);

  // 文件名，比如 transaction_12345.json
  const filename = `transaction_${tx.transactionId}.json`;


  const dir = "/datax/";
 
  const filepath = path.resolve(dir, filename);

  // 检查目录是否存在，不存在则递归创建
  if (!fs.existsSync(dir)) {
    fs.mkdirSync(dir, { recursive: true });
  }
  

   
  // 同步写入文件
  fs.writeFileSync(filepath, json, { encoding: "utf-8" });

  console.log(`Transaction saved to ${filepath}`);
}
