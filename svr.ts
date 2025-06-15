import express from "express";
import { handleOptionsReq } from "./rest/restUti.ts";
const app = express();
const port = 3000; // 你可以自定义端口

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

  // setContextThrd(exchange);

  try {
    //---------blk chk auth
    urlAuthChk();

    //============aop trans begn
    callInTx( (em:any) => {
      // hdl
      handlexProcess();
    });
    //  System.out.println("✅endfun handle()");
    return;

  } catch (e8) {
    // transactionThreadLocal.get().rollback();
    rollbackTx();
    exHdlr(e8)

  } finally {
    closeConns()
  }
  //end catch


  // const { uname, pwd } = req.query;
  // //get dto
  // if (!uname || !pwd) {
  //   return res.status(400).json({ error: "用户名或密码缺失" });
  // }

  // // 这里可以添加你的验证逻辑
  // console.log(`Login attempt: uname=${uname}, pwd=${pwd}`);

  // res.json({ message: "登录成功", username: uname });

}

function urlAuthChk() { }
function beginx() {
  throw new Error("Function not implemented.");
}

function urlAuthChkV2(exchange: any) {
  throw new Error("Function not implemented.");
}

function needLoginUserAuth(target: any) {
  throw new Error("Function not implemented.");
}

function getCurrentUser(): any {
  throw new Error("Function not implemented.");
}

function handlexProcess() {
  throw new Error("Function not implemented.");
}

function commitx() {
  throw new Error("Function not implemented.");
}

function rollbackTx() {
  throw new Error("Function not implemented.");
}

function printStackTrace(e8: any) {
  throw new Error("Function not implemented.");
}

function getRawEx(e8: any): any {
  throw new Error("Function not implemented.");
}

function getExceptionObjFrmE(e: any): any {
  throw new Error("Function not implemented.");
}

function addInfo2ex(ex: any, e: any) {
  throw new Error("Function not implemented.");
}

function createErrResponseWzErrcode(ex: any): any {
  throw new Error("Function not implemented.");
}

function encodeJson4ex(errResponseWzErrcode: any): any {
  throw new Error("Function not implemented.");
}

function wrtRespErr(exchange: any, responseTxt: any) {
  throw new Error("Function not implemented.");
}

function exHdlr(e8: any) {
  throw new Error("Function not implemented.");
}

function closeConns() {
  throw new Error("Function not implemented.");
}

 

function callInTx(arg0: (em: any) => void) {
  throw new Error("Function not implemented.");
}

