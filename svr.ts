//import express from "express";
 

import express from 'express';

// 如果你需要类型（在 TypeScript 中）
import type { Request, Response, NextFunction } from 'express';

import { handleOptionsReq, regMap } from "./rest/restUti.ts";
import { aopFltr } from "./rest/AopIntr.ts";
const app = express();
const port = 3000; // 你可以自定义端口

console.log("pre use")


//app.get("/", hdlAllReqFun);
app.use(aopFltr)   // 中间件：拦截所有请求  all path,all method
app.options("/", handleOptionsReq)

console.log("pre lstn")
app.listen(port, () => {
  console.log(`Server running at http://localhost:${port}`);
});



// pathMthMap = new Map<string, () => void>();
/**
 * 
 * @param req 
 * @param res 
 * @returns 
 */
function hdlAllReqFun(req: Request, res: Response) {
  

  console.log("fun hdlallxx")
//  const path = req.path; // Express 提供的标准属性
//     const methodFn = pathMthMap.get(path);
           

//              if (methodFn) {
//         registerMapping(path, methodFn, req,res);
//     } else {
//         res.status(404).send("Not found");
//     }
           

  //regMap(path,)
  // const { uname, pwd } = req.query;
  // //get dto
  // if (!uname || !pwd) {
  //   return res.status(400).json({ error: "用户名或密码缺失" });
  // }

  // // 这里可以添加你的验证逻辑
  // console.log(`Login attempt: uname=${uname}, pwd=${pwd}`);

  // res.json({ message: "登录成功", username: uname });

}
function registerMapping(path: any, method: any, req: any, res: any) {
  throw new Error("Function not implemented.");
}

