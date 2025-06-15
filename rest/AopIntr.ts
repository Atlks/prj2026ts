
import express, { Request, Response, NextFunction } from "express";
export function aopFltr(req: Request, res: Response, next: NextFunction)
{
    console.log(`[${new Date().toISOString()}] ${req.method} ${req.url}`);
    // setContextThrd(exchange);
  try {
    //---------blk chk auth
    urlAuthChk();

    //============aop trans begn
    callInTx( (em:any) => {
      // hdl
     next(); // 放行到下一个中间件或路由
    });
    //  System.out.println("✅endfun handle()");
    return;

  } catch (e8) {
    
    exHdlr(e8)

  } finally {
    closeConns()
  }
  //end catch

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

