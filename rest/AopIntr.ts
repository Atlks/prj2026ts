
import express from 'express';
import type { Request, Response, NextFunction } from 'express';
export function aopFltr(req: Request, res: Response, next: NextFunction) {
    console.log(`[${new Date().toISOString()}] ${req.method} ${req.url}`);
    // setContextThrd(exchange);

    if (req.method === 'OPTIONS') {
        //  handleOptionsReq(req,res)
        return next(); // 放行 OPTIONS 请求，不拦截
    }


    try {
        //---------blk chk auth
        urlAuthChk();

        //============aop trans begn
        callInTx((em: any) => {
            // hdl
            // next(); // 放行到下一个中间件或路由
            registerMappingAllpath(req, res)
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
// 假设有一个 Map<string, Function> 映射路径到处理函数
const pathMthMap = new Map<string, (req: Request, res: Response) => void>();
function registerMappingAllpath(req: Request, res: Response) {


    console.log("fun hdlall")
    const path = req.path; // Express 提供的标准属性
    const methodFn = pathMthMap.get(path);


    if (methodFn) {
        registerMapping(path, methodFn, req, res);
    } else {
        res.status(404).send("Not found");
    }
}

function registerMapping(path: any, method: any, req: any, res: any) {
    console.log("fun regmap ,path=" + path)
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
    console.log(e8)
}

function closeConns() {
    console.log("fun cls conn")
}



function callInTx(arg0: (em: any) => void) {
    console.log("fun call in tx")
    arg0.apply(null)
}

