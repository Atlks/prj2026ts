import { Request, Response, NextFunction } from "express";

/**
 * 设置跨域响应头（中间件）
 */ 
  export function  handleOptionsReq(req: Request, res: Response)   {
        setCrossDomain(req,res);
          res.header("Allow", "GET, POST, PUT, DELETE, OPTIONS");
        res.sendStatus(204); // No Content
    }


     /**
 * 设置跨域响应头（中间件）
 */
export function setCrossDomain(req: Request, res: Response): void {
    res.header("Access-Control-Allow-Origin", "*");
    res.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
    res.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
    res.header("Access-Control-Allow-Credentials", "true");
}

/**
 * 
 * @param path 
 * @param String 
 * @param fun1 
 * @returns 
 */
export  function regMap(path: string, fun1: (arg: any) => void): string {
    console.log("fun regmap(")
    console.log(path);
    fun1(path); // 确保 fun1 函数被正确调用
    return "ok..ret from regmap";
}
