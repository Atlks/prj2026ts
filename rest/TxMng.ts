
import { sqlSessionFactory } from "./ProcessContext.ts";
import { FunContext } from "../model/FunContext.ts";

function callInTx(fun1: (em: any) => void) {
    console.log("fun call in tx")
    fun1("")
}

export async function callInTransaction<T>(fx: (session: any, ctx: FunContext) => Promise<T>): Promise<T> {
    console.log("runInTransaction");

    var em =1; //=  sqlSessionFactory.openSession(false);
   // ThreadContext.sqlSessionThreadLocal.set(em);
   // console.log("AutoCommit 状态: ", await em.getConnection().getAutoCommit());

    try {
        console.log("begin");

        // const funCtx = new FunContext();
        // funCtx.sqlSession = em;

        // const result = await fx(em, funCtx);

      //  await em.commit();
        console.log("commit");
        console.log("endfun runInTransaction");
      
        return 1 as T;
    } catch (e) {
       // await em.rollback();
        console.log("rollback");
        throw e; // 在 TypeScript 中，异常处理仍然适用
    } finally {
        closeConn();
    }
}

function closeConn() {
    throw new Error("Function not implemented.");
}
