
const sql = "SELECT * FROM users WHERE uname='aaa' AND age>5";

// 使用正则表达式提取 WHERE 子句中的条件
const match = sql.match(/WHERE (.+)/i);
if (match) {
    const conditions = match[1].split(" AND ");
    conditions.forEach((condition, index) => {
        console.log(`条件${index + 1}: ${condition.trim()}`);
    });
}