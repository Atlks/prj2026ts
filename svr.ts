import express from "express";
import { handleOptionsReq, regMap } from "./rest/restUti.ts";
import { aopFltr } from "./rest/AopIntr.ts";
const app = express();
const port = 3000; // 你可以自定义端口


// 中间件：拦截所有请求
app.use(aopFltr)
app.get("/", hdlAllReqFun);
app.options("/", handleOptionsReq)
app.listen(port, () => {
  console.log(`Server running at http://localhost:${port}`);
});

/**
 * 
 * @param req 
 * @param res 
 * @returns 
 */
function hdlAllReqFun(req: any, res: any) {
  

  var path=req.getPath()
   var method = pathMthMap.get(path);
        
            registerMapping(path, method, req,res);
           

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

