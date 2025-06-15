

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
